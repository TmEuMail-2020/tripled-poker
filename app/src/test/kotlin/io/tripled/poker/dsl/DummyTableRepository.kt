package io.tripled.poker.dsl

import io.tripled.poker.app.api.response.Cards
import io.tripled.poker.app.api.response.HiddenCards
import io.tripled.poker.app.api.response.Player
import io.tripled.poker.app.api.response.VisibleCards
import io.tripled.poker.domain.*
import io.tripled.poker.domain.cards.Card
import io.tripled.poker.domain.cards.Hand
import io.tripled.poker.domain.game.*
import io.tripled.poker.domain.table.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.DslProjection
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

class DummyTableRepository(private val eventStore: EventStore) : TableRepository {
    override fun findTableById(tableId: TableId) = Table(TableReducer().of(eventStore.findById(tableId)))

    override fun saveTable(tableId: TableId, events: List<Event>)  = eventStore.append(tableId, events)

    override fun projectTable(playerName: PlayerId): io.tripled.poker.app.api.response.Table = TableProjectionReducer().table(playerName, eventStore)

    internal class TableReducer() {
        fun of(events: List<Event>) = TableState(players(events))

        private fun players(events: List<Event>): List<String> = events
                .filterEvents<PlayerJoinedTable>()
                .map { event -> event.name }
    }

    internal class TableProjectionReducer {

        fun table(playerName: PlayerId, eventStore: EventStore): io.tripled.poker.app.api.response.Table {
            val tableEvents = mergeTableAndActiveGameStream(eventStore.findById("1"), eventStore)
            return InnerTableProjection(playerName, dsl(eventStore), tableEvents).table
        }

        private fun dsl(eventStore: EventStore): String = DslProjection(eventStore).dsl()

        private fun mergeTableAndActiveGameStream(tableEvents: List<Event>, eventStore: EventStore): List<Event> {
            tableEvents
                    .filterEvents<GameCreated>()
                    .lastOrNull()?.apply {
                        val currentlyActiveGameEvents = eventStore.findById(gameId)
                        return tableEvents.union(currentlyActiveGameEvents).toList()
                    }
            return tableEvents
        }

        private class InnerTableProjection(private val playerName: String, dsl: String, events: List<Event>) {
            val table: io.tripled.poker.app.api.response.Table

            init {
                table = io.tripled.poker.app.api.response.Table(players(events), flop(events), turn(events), river(events), winner(events), dsl)
            }

            private fun flop(events: List<Event>): Cards = events.filterEvents<FlopIsTurned>()
                    .map { t -> VisibleCards(listOf(t.card1.mapToCard(), t.card2.mapToCard(), t.card3.mapToCard())) }
                    .firstOrNull() ?: HiddenCards(3)

            private fun turn(events: List<Event>): Cards = events.filterEvents<TurnIsTurned>()
                    .map { t -> VisibleCards(listOf(t.card.mapToCard())) }
                    .firstOrNull() ?: HiddenCards(1)

            private fun river(events: List<Event>): Cards = events.filterEvents<RiverIsTurned>()
                    .map { t -> VisibleCards(listOf(t.card.mapToCard())) }
                    .firstOrNull() ?: HiddenCards(1)

            private fun winner(events: List<Event>): Player? {
                return when (val winner = playerWonGame(events)) {
                    noWinner() -> null
                    else -> playerWithCards(events, winner.name) { hand -> hand.asVisibleCards() }
                }
            }

            private fun playerWonGame(events: List<Any>): PlayerWonGame? = events.lastOrNull { it is PlayerWonGame } as PlayerWonGame?

            private fun players(events: List<Event>) = events
                    .filterEvents<PlayerJoinedTable>()
                    .map { event ->
                        val name = event.name
                        val playerWithCards = playerWithCards(events, name, obfuscateCards(name))
                        Player(playerWithCards.name, playerWithCards.cards)
                    }

            private fun obfuscateCards(name: String): (Hand) -> Cards = { hand ->
                when {
                    itsMe(name) -> hand.asVisibleCards()
                    else -> hand.asHiddenCards()
                }
            }

            private fun itsMe(name: String) = name == playerName

            private fun playerWithCards(events: List<Event>, player: String, cardMapper: (Hand) -> Cards) = when (val hand = getPlayerHand(events, player)) {
                noHand() -> Player(player)
                else -> Player(player, cardMapper(hand))
            }

            private fun noHand() = null
            private fun noWinner() = null

            private fun getPlayerHand(events: List<Event>, player: String) =
                    events.lastEventOrNull<HandsAreDealt>()?.hands?.let { it[player] }

            private fun Hand.asVisibleCards() = VisibleCards(mapToCards())
            private fun Hand.asHiddenCards() = HiddenCards(cards().size)
            private fun Hand.mapToCards() = cards().map { it.mapToCard() }
            private fun Card.mapToCard() = io.tripled.poker.vocabulary.Card(this.suit, this.value)

        }
    }

}