package io.tripled.poker.domain

import io.tripled.poker.vocabulary.PlayerId

class WinnerDeterminer {
    fun determineWinner(playerCards: Map<PlayerId, Hand>, commonCards: List<Card>): PlayerId {
        return determineWinner(playerCards)
    }

    private fun determineWinner(dealtCards: Map<PlayerId, Hand>) = dealtCards
            .toList()
            .maxBy { it.second.card1.score + it.second.card2.score }!!
            .first
}