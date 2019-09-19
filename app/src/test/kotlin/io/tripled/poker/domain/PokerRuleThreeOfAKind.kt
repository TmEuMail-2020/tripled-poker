package io.tripled.poker.domain

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.vocabulary.Value.*
import io.tripled.poker.vocabulary.Suit.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PokerRuleThreeOfAKindTest {

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("threeOfAKindRule")
    fun threeOfAKindRule(name: String, cards: List<Card>, expected: Boolean) {
        val matches = ThreeOfAKindRule().matches(cards)

        expect(matches).toBe(expected)
    }

    companion object {
        @JvmStatic
        private fun threeOfAKindRule() = Stream.of(
                Arguments.of("if 3 cards are the same we have 3 of a kind",
                        listOf(KING of CLUBS, KING of DIAMONDS, KING of SPADES), true),
                Arguments.of("3 different cards are not 3 of a kind",
                        listOf(JACK of CLUBS, QUEEN of DIAMONDS, KING of SPADES), false),
                Arguments.of("One card is not 3 of a kind",
                        listOf(KING of CLUBS), false),
                Arguments.of("if 4 cards are of the same value we have three of a kind",
                        listOf(KING of CLUBS, KING of DIAMONDS, KING of HEARTS, KING of SPADES), true)
        )
    }

}

class ThreeOfAKindRule {
    fun matches(cards: List<Card>): Boolean
            = cards
            .groupBy { it.value }
            .values
            .any { it.size >= 3 }

}