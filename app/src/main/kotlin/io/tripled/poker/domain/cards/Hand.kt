package io.tripled.poker.domain.cards

import io.tripled.poker.domain.cards.Card

data class Hand(val card1: Card, val card2: Card) {
    fun cards() = listOf(card1, card2)
}