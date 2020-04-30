package io.tripled.poker.vocabulary

// Identities
typealias PlayerId = String
typealias TableId = String
typealias GameId = String

// Enums
enum class Suit { DIAMONDS, SPADES, HEARTS, CLUBS }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }

// VO
data class Card(val suit: Suit, val value: Value)