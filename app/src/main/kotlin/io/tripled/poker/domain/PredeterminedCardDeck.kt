package io.tripled.poker.domain

import java.util.*

class PredeterminedCardDeck(
    _cards: List<Card>
) : Deck {
    private val queue = LinkedList(_cards)

    override val cards: List<Card>
        get() = queue.toList()

    override fun dealCard() = queue.pop()

    fun provideNewCards(newCards: List<Card>) {
        queue.clear()
        queue.addAll(newCards)
    }
}