package io.tripled.poker.domain

import io.tripled.poker.vocabulary.Value.*
import io.tripled.poker.vocabulary.Suit.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardTests {

    @Test
    internal fun `suits are taken into account when comparing cards`() {
        val cards = listOf(TWO of HEARTS,
                TWO of SPADES,
                TWO of CLUBS,
                ACE of HEARTS,
                TWO of DIAMONDS)

        val sortedCards = cards.sortedByDescending { it.score }

        assertEquals(listOf(
                ACE of HEARTS,
                TWO of CLUBS,
                TWO of HEARTS,
                TWO of SPADES,
                TWO of DIAMONDS), sortedCards)
    }
}