package io.tripled.poker.domain

import java.util.*

class PredeterminedCardDeck(
    _cards: List<Card>
) : Deck {
    override val cards: List<Card>
        get() = queue.toList()

    val queue = LinkedList(_cards)

    override fun dealCard() = queue.pop()

    fun provideNewCards(deckOfHearts: List<Card>) {
        queue.clear()
        queue.addAll(deckOfHearts)
    }
}