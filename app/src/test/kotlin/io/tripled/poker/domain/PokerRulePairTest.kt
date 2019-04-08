package io.tripled.poker.domain

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.response.Suit.*
import io.tripled.poker.api.response.Value
import io.tripled.poker.api.response.Value.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class PokerRulePairTest {

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("onePairRule")
    fun onePairRule(name: String, cards: List<Card>, expected: Boolean) {
        val matches = PairRule(1).matches(cards)

        expect(matches).toBe(expected)
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("twoPairRule")
    fun twoPairRule(name: String, cards: List<Card>, expected: Boolean) {
        val matches = PairRule(2).matches(cards)

        expect(matches).toBe(expected)
    }

    companion object {
        @JvmStatic
        private fun onePairRule() = Stream.of(
                Arguments.of("if 2 cards are of the same value we have a pair",
                        listOf(KING of CLUB, KING of DIAMOND), true),
                Arguments.of("if 2 cards are of different value we don't have a pair",
                        listOf(QUEEN of CLUB, KING of DIAMOND), false),
                Arguments.of("if there are 7 cards and none of them are the same",
                        (1..7).map {
                            Value.values()[it] of DIAMOND
                        }, false),
                Arguments.of("if there are 7 cards and the last two are a pair",
                        (1..6).map {
                            Value.values()[it] of DIAMOND
                        } + (Value.values()[6] of HEART), true)
        )

        @JvmStatic
        private fun twoPairRule() = Stream.of(
                Arguments.of("if we have a pair of kings and a pair of aces value we have two pair",
                        listOf(KING of CLUB, KING of DIAMOND, ACE of HEART, ACE of SPADES), true),
                Arguments.of("if 4 cards are of the same value we have two pair",
                        listOf(KING of CLUB, KING of DIAMOND, KING of HEART, KING of SPADES), true),
                Arguments.of("if there are 7 cards and none of them are the same",
                        (1..7).map {
                            Value.values()[it] of DIAMOND
                        }, false),
                Arguments.of("if 2 cards are of different value we don't have a pair",
                        listOf(QUEEN of CLUB, KING of DIAMOND), false),
                Arguments.of("if there are 7 cards and the last two are a pair",
                        (1..6).map {
                            Value.values()[it] of DIAMOND
                        } + (Value.values()[6] of HEART), false)
        )
    }



}

class PairRule(private val amountOfPairs: Int) {
    fun matches(cards: List<Card>): Boolean
        = cards
            .groupBy { it.value }
            .values
            .map { Math.floor(it.size / 2.0) }
            .sum() >= amountOfPairs
}
