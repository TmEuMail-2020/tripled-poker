package io.tripled.poker.domain

import io.tripled.poker.domain.cards.ShuffledDeck
import io.tripled.poker.domain.cards.of
import io.tripled.poker.vocabulary.Suit
import io.tripled.poker.vocabulary.Value
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ShuffledDeckTest {

    @Test
    internal fun `all cards are dealt`() {
        val shuffledDeck = ShuffledDeck()
        val cards = (1..52).map { shuffledDeck.dealCard() }

        Suit.values().forEach { suit ->
            Value.values().forEach { value ->
                assertTrue(cards.contains(value of suit))
            }
        }
    }

    @Test
    internal fun `cards are really shuffled`() {
        val unique = (1..100).map { ShuffledDeck().dealCard().score }.distinct();

        assertNotEquals(1, unique.size)
    }
}