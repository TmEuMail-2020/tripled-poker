package io.tripled.poker.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardTests {

    @Test
    internal fun `suits are taken into account when comparing cards`() {
        val cards = listOf(Card(Suit.HEART, Value.TWO),
                Card(Suit.CLOVER, Value.TWO),
                Card(Suit.CLUB, Value.TWO),
                Card(Suit.HEART, Value.ACE),
                Card(Suit.DIAMOND, Value.TWO))

        val sortedCards = cards.sortedByDescending { it.score }

        assertEquals(listOf(
                Card(Suit.HEART, Value.ACE),
                Card(Suit.CLUB, Value.TWO),
                Card(Suit.HEART, Value.TWO),
                Card(Suit.CLOVER, Value.TWO),
                Card(Suit.DIAMOND, Value.TWO)), sortedCards)
    }
}