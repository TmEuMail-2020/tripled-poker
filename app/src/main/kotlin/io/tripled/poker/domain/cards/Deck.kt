package io.tripled.poker.domain.cards

import io.tripled.poker.domain.cards.Card

interface Deck {
    val cards: List<Card>

    fun dealCard(): Card
}
