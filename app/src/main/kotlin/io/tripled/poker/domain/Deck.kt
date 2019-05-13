package io.tripled.poker.domain

interface Deck {
    val cards: List<Card>

    fun dealCard(): Card
}
