package io.tripled.poker.domain.cards

interface Deck {
    val cards: List<Card>

    fun dealCard(): Card
}
