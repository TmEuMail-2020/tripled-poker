package io.tripled.poker.domain

interface Deck {
    fun dealCard(): Card
    fun remainingCards(): List<Card>
}
