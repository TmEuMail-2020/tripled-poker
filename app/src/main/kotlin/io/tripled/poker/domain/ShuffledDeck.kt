package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value

class ShuffledDeck : Deck {
    private val cards = Suit.values()
            .flatMap { suit ->
                Value.values()
                        .map { value -> Card(suit, value) }
            }.toMutableList()

    init {
        cards.shuffle()
    }

    override fun dealCard() = cards.removeAt(0)
}