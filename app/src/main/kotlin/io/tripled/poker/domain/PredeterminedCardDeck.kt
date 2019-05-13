package io.tripled.poker.domain

import java.util.*

class PredeterminedCardDeck(
    val _cards: List<Card>
) : Deck {
    override val cards: List<Card>
        get() = queue.toList()

    val queue = LinkedList<Card>(_cards)

    override fun dealCard() = queue.pop()
}