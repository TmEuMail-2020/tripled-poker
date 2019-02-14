package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value

data class PlayerJoinedTable(val name: String)
data class RoundStarted(val noop: String = "")
data class CardsAreDealt(val cards: Map<String, Card>)
data class PlayerWonRound(val name: String)

data class Card(val suit: Suit, val value: Value) {
    val score: Int
        get() = value.ordinal * 100 + suit.ordinal
}

class Table(events: List<Any>) {
    private val players = players(events)

    private fun players(events: List<Any>): List<String> {
        return events
                .filter { it is PlayerJoinedTable }
                .map { event ->
                    (event as PlayerJoinedTable).name
                }
    }

    fun join(name: String) = listOf<Any>(PlayerJoinedTable(name))

    fun startRound(deck: Deck): List<Any> {
        return if (players.size > 1) {
            val dealtCards = dealCards(deck)
            listOf(
                    RoundStarted(),
                    CardsAreDealt(dealtCards),
                    PlayerWonRound(determineWinner(dealtCards))
            )
        } else listOf()
    }

    private fun determineWinner(dealtCards: Map<String, Card>) = dealtCards
            .toList()
            .maxBy { it.second.score }!!
            .first

    private fun dealCards(deck: Deck) = players.associateWith { deck.dealCard() }
}