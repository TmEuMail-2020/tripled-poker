package io.tripled.poker.projection

import io.tripled.poker.api.response.*
import io.tripled.poker.api.response.Table
import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore

class TableProjection {

    fun table(playerName: String, eventstore: EventStore): Table {
        val tableEvents = eventstore.findById(1)
        tableEvents
                .filterEvents<GameStarted>()
                .lastOrNull()?.apply {
                    val currentlyActiveGameEvents = eventstore.findById(gameId)
                    return InnerTableProjection(playerName, tableEvents.union(currentlyActiveGameEvents).toList()).table
                }

        return InnerTableProjection(playerName, tableEvents).table
    }

    private class InnerTableProjection(private val playerName: String, events: List<Event>) {
        val table: Table

        init {
            table = Table(players(events), winner(events))
        }

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

        private fun io.tripled.poker.domain.Hand.asVisibleCards() = VisibleCards(mapToCards())
        private fun io.tripled.poker.domain.Hand.asHiddenCards() = HiddenCards(cards().size)
        private fun io.tripled.poker.domain.Hand.mapToCards() = cards().map { it.mapToCard() }
        private fun io.tripled.poker.domain.Card.mapToCard() = io.tripled.poker.api.response.Card(this.suit, this.value)

    }
}

