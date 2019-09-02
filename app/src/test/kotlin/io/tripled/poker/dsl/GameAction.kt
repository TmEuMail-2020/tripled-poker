package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.domain.Event
import io.tripled.poker.domain.GameId
import io.tripled.poker.domain.PlayerChecked
import io.tripled.poker.domain.PlayerId

class GameAction(private val gameId: GameId,
                 private val gameUseCases: GameService,
                 val expectedEvents: ArrayList<Event> = ArrayList()) {

    fun PlayerId.checks() = check(this)

    private fun check(playerId: PlayerId) {
        gameUseCases.check(playerId)
        expectedEvents += PlayerChecked(playerId)
    }


    // TODO fold, raise, all-in, etc
}