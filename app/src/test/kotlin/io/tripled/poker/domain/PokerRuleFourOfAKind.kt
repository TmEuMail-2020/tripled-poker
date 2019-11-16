package io.tripled.poker.domain

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.domain.cards.Card
import io.tripled.poker.domain.cards.of
import io.tripled.poker.vocabulary.Suit.*
import io.tripled.poker.vocabulary.Value.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PokerRuleFourOfAKindTest {

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("fourOfAKindRule")
    fun fourOfAKindRule(name: String, cards: List<Card>, expected: Boolean) {
        val matches = FourOfAKindRule().matches(cards)

        expect(matches).toBe(expected)
    }

    companion object {
        @JvmStatic
        private fun fourOfAKindRule() = Stream.of(
                Arguments.of("if 4 cards are the same we have 4 of a kind",
                        listOf(KING of CLUBS, KING of DIAMONDS, KING of HEARTS, KING of SPADES), true),
                Arguments.of("if only 3 cards are the same we are not 4 of a kind",
                        listOf(KING of CLUBS, KING of DIAMONDS, KING of HEARTS, ACE of SPADES), false),
                Arguments.of("4 different cards are not 4 of a kind",
                        listOf(JACK of CLUBS, QUEEN of DIAMONDS, KING of SPADES, ACE of SPADES), false),
                Arguments.of("One card is not 4 of a kind",
                        listOf(KING of CLUBS), false)
        )
    }

}

class FourOfAKindRule {
    fun matches(cards: List<Card>): Boolean
            = cards
            .groupBy { it.value }
            .values
            .any { it.size >= 4 }

}