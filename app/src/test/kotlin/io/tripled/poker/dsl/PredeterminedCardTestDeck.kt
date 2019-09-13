package io.tripled.poker.dsl

import io.tripled.poker.domain.Card
import io.tripled.poker.domain.Deck
import java.util.*

class PredeterminedCardTestDeck(
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