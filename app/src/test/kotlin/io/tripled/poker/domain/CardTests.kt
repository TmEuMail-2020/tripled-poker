package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit.*
import io.tripled.poker.api.response.Value.ACE
import io.tripled.poker.api.response.Value.TWO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardTests {

    @Test
    internal fun `suits are taken into account when comparing cards`() {
        val cards = listOf(TWO of HEART,
                TWO of SPADES,
                TWO of CLUB,
                ACE of HEART,
                TWO of DIAMOND)

        val sortedCards = cards.sortedByDescending { it.score }

        assertEquals(listOf(
                ACE of HEART,
                TWO of CLUB,
                TWO of HEART,
                TWO of SPADES,
                TWO of DIAMOND), sortedCards)
    }
}