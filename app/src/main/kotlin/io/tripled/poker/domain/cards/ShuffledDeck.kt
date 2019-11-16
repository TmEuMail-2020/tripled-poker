package io.tripled.poker.domain.cards

import io.tripled.poker.vocabulary.Suit
import io.tripled.poker.vocabulary.Value

class ShuffledDeck : Deck {
    override val cards = Suit.values()
            .flatMap { suit ->
                Value.values()
                        .map { value -> Card(suit, value) }
            }.toMutableList()

    init {
        cards.shuffle()
    }

    override fun dealCard() = cards.removeAt(0)
}