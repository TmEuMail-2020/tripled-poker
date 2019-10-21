package io.tripled.poker.domain

import io.tripled.poker.vocabulary.PlayerId

class BestHandsCalculator {
    fun calculateBest(playerCards: Map<PlayerId, Hand>, commonCards: List<Card>): PlayerId {
        return calculateBest(playerCards)
    }

    private fun calculateBest(dealtCards: Map<PlayerId, Hand>) = dealtCards
            .toList()
            .maxBy { it.second.card1.score + it.second.card2.score }!!
            .first
}