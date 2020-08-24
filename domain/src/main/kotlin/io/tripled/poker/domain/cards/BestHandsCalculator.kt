package io.tripled.poker.domain.cards

import io.tripled.poker.vocabulary.PlayerId

class BestHandsCalculator {
    fun calculateBest(playerCards: Map<PlayerId, Hand>, commonCards: List<Card>): PlayerId {
        return calculateBest(playerCards)
    }

    private fun calculateBest(dealtCards: Map<PlayerId, Hand>) = dealtCards
            .toList()
            .maxByOrNull { it.second.card1.score + it.second.card2.score }!!
            .first
}