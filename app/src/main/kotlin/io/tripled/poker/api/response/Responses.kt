package io.tripled.poker.api.response

enum class Suit { DIAMOND, SPADES, HEART, CLUB, HIDDEN }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE, HIDDEN }
data class Card(val suit: Suit, val value: Value){
    companion object {
        val HIDDEN = Card(Suit.HIDDEN, Value.HIDDEN)
    }
}
data class Player(val name: String, val cards: List<Card> = listOf())
data class Table(val players: List<Player>, val winner: Player? = null)