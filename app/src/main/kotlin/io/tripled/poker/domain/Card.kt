package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value

data class Card(val suit: Suit, val value: Value) {
    val score: Int
        get() = value.ordinal * 100 + suit.ordinal
}

infix fun Value.of(suit: Suit) = Card(suit, this)
