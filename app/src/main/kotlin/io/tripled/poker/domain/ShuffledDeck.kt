package io.tripled.poker.domain

class ShuffledDeck : Deck {
    private val cards = Suit.values().flatMap {  suit ->
        Value.values().map { value ->
            Card(suit, value)
        }
    }.toMutableList()

    init {
        cards.shuffle()
    }

    override fun dealCard() = cards.removeAt(0)
}