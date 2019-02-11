package io.tripled.poker.domain

class DummyDeck: Deck {
    override fun dealCard() = Card(Suit.HEART, Value.ACE)
}