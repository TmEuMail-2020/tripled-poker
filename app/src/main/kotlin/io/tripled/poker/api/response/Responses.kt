package io.tripled.poker.api.response

enum class Suit { DIAMOND, SPADES, HEART, CLUB }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
data class Card(val value: Value, val suit: Suit)
data class Player(val name: String, val cards: List<Card> = listOf())
data class Table(val players: List<Player>, val winner: Player? = null)