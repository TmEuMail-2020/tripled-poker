package io.tripled.poker.projection

import io.tripled.poker.api.response.*
import io.tripled.poker.domain.CardsAreDealt
import io.tripled.poker.domain.Hand
import io.tripled.poker.domain.PlayerJoinedTable
import io.tripled.poker.domain.PlayerWonRound

class TableProjection(private val playerName: String, events: List<Any>) {

    val table: Table

    init {
        table = Table(players(events), winner(events))
    }

    private fun winner(events: List<Any>): Player? {
        return when (val winner = playerWonRound(events)) {
            noWinner() -> null
            else -> playerWithCards(events, winner.name) { hand -> hand.asVisibleCards() }
        }
    }

    private fun playerWonRound(events: List<Any>): PlayerWonRound? = events.lastOrNull { it is PlayerWonRound } as PlayerWonRound?

    private fun players(events: List<Any>) = events
            .filter { it is PlayerJoinedTable }
            .map { event ->
                val name = (event as PlayerJoinedTable).name
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

    private fun playerWithCards(events: List<Any>, player: String, cardMapper: (Hand) -> Cards) = when (val hand = getPlayerHand(events, player)) {
        noHand() -> Player(player)
        else -> Player(player, cardMapper(hand))
    }

    private fun noHand() = null
    private fun noWinner() = null

    private fun getPlayerHand(events: List<Any>, player: String) =
            (events.lastOrNull { it is CardsAreDealt } as CardsAreDealt?)?.hands?.let { it[player] }


    private fun io.tripled.poker.domain.Hand.asVisibleCards() = VisibleCards(mapToCards())
    private fun io.tripled.poker.domain.Hand.asHiddenCards() = HiddenCards(cards().size)
    private fun io.tripled.poker.domain.Hand.mapToCards() = cards().map { it.mapToCard() }
    private fun io.tripled.poker.domain.Card.mapToCard() = io.tripled.poker.api.response.Card(this.suit, this.value)

}
