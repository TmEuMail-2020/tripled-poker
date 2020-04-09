package io.tripled.poker.infra.eventsourcing

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
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId
import org.springframework.stereotype.Repository

@Repository
class MongoRepositories(private val eventStore: EventStore) : GameRepository, TableRepository {
    override fun saveTable(tableId: TableId, events: List<Event>)  = eventStore.append(tableId, events)

    override fun save(gameId: GameId, events: List<Event>) = eventStore.append(gameId, events)

    override fun projectTable(playerName: PlayerId): io.tripled.poker.app.api.response.Table = TableProjectionReducer().table(playerName, eventStore)

    override fun findTableById(tableId: TableId) = Table(TableReducer().of(eventStore.findById(tableId)))

    override fun findGameById(gameId: GameId) = Game(GameReducer().of(eventStore.findById(gameId)))

   internal class GameReducer {
        fun of(events: List<Event>) = GameState(players(events),
                hands(events),
                remainingCards(events),
                countChecks(events),
                phase(events),
                lastPlayer(events))

        private fun countChecks(events: List<Event>): Int =
                events.filterEvents<PlayerChecked>().size

        private fun lastPlayer(events: List<Event>): PlayerId? {
            return events.filter { e -> e is PlayerFolded || e is PlayerChecked }.lastOrNull { e ->
                return when (e) {
                    is PlayerFolded -> e.name
                    is PlayerChecked -> e.name
                    else -> ""
                }
            }?.toString() ?: players(events).lastOrNull()

        }

        private fun players(events: List<Event>): List<PlayerId> {
            val allPlayers = hands(events).keys.toList()
            val foldedPlayers = events.filterEvents<PlayerFolded>().map { t -> t.name }

            return allPlayers - foldedPlayers
        }

        private fun hands(events: List<Event>): Map<PlayerId, Hand> {
            val lastEventOrNull = events
                    .lastEventOrNull<HandsAreDealt>()
            return lastEventOrNull?.hands ?: mapOf()
        }

        private fun remainingCards(events: List<Event>): List<Card> {
            val lastEventOrNull = events
                    .lastEventOrNull<GameStarted>() ?: return listOf()

            val cards = lastEventOrNull.cardsInDeck.toMutableList()
            return events.fold(cards) { _, event ->
                when (event) {
                    is HandsAreDealt -> cards.removeAll(
                            event.hands.flatMap { it.value.cards() }
                    )
                    is FlopIsTurned -> cards.removeAll(
                            listOf(event.card1, event.card2, event.card3)
                    )
                    is TurnIsTurned -> cards.remove(event.card)
                    is RiverIsTurned -> cards.remove(event.card)
                }
                cards
            }
        }

        private fun phase(events: List<Event>): GamePhase =
                events.fold(GamePhase.PRE_FLOP, { acc, event ->
                    when (event) {
                        is FlopIsTurned -> GamePhase.FLOP
                        is TurnIsTurned -> GamePhase.TURN
                        is RiverIsTurned -> GamePhase.RIVER
                        is PlayerWonGame -> GamePhase.DONE
                        else -> acc
                    }
                })
    }

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

        private class InnerTableProjection(private val playerName: PlayerId, dsl: String, events: List<Event>) {
            val table: io.tripled.poker.app.api.response.Table

            init {
                table = io.tripled.poker.app.api.response.Table(playerName, players(events), flop(events), turn(events), river(events), winner(events), dsl)
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