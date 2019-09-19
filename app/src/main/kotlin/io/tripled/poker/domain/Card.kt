package io.tripled.poker.domain

import io.tripled.poker.vocabulary.Suit
import io.tripled.poker.vocabulary.Value

data class Card(val suit: Suit, val value: Value) {
    val score: Int
        get() = value.ordinal * 100 + suit.ordinal

    infix fun and(card: Card): Hand = Hand(this, card)
}

infix fun Value.of(suit: Suit) = Card(suit, this)
