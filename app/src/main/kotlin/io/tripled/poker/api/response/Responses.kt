package io.tripled.poker.api.response

import io.tripled.poker.domain.PlayerId

enum class Suit { DIAMONDS, SPADES, HEARTS, CLUBS }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
data class Card(val suit: Suit, val value: Value)
data class Player(val name: PlayerId, val cards: Cards = HiddenCards(0))
data class Table(val players: List<Player>,
                 val flop: Cards,
                 val turn: Cards,
                 val river: Cards,
                 val winner: Player? = null,
                 val dsl: String)

interface Cards {
    val numberOfCards: Int
    val visibleCards: List<Card>
}

data class VisibleCards(override val visibleCards: List<Card>) : Cards {
    override val numberOfCards: Int
        get() = visibleCards.size
}

data class HiddenCards(override val numberOfCards: Int): Cards {
    override val visibleCards: List<Card>
        get() = emptyList()
}