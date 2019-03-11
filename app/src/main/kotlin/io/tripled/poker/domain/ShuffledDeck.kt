package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value

class ShuffledDeck : Deck {
    private val cards = Suit.values()
            .filter { suit -> suit != Suit.HIDDEN}
            .flatMap { suit -> Value.values()
                    .filter { v -> v != Value.HIDDEN}
                    .map { value -> Card(suit, value) }
    }.toMutableList()

    init {
        cards.shuffle()
    }

    override fun dealCard() = cards.removeAt(0)
}