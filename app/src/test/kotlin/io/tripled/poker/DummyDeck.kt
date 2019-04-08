package io.tripled.poker

import io.tripled.poker.domain.Card
import io.tripled.poker.domain.Deck
import java.util.*

class DummyDeck : Deck {

    val queue = LinkedList<Card>()

    override fun dealCard() = queue.pop()
}