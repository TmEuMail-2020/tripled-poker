package io.tripled.poker.domain

class ShuffledDeck : Deck {
    private val cards = mutableListOf<Card>()

    init {
        Suit.values().forEach { suit ->
            Value.values().forEach { value ->
                cards += Card(suit, value)
            }
        }
        cards.shuffle()
    }

    override fun dealCard() = cards.removeAt(0)
}