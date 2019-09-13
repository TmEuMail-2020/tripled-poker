package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.domain.*

class GameAction(private val tableId: TableId,
                 private val gameId: GameId,
                 private val gameUseCases: GameService,
                 val expectedEvents: ArrayList<Event> = ArrayList()) {

    fun PlayerId.checks() = check(this)

    private fun check(playerId: PlayerId) {
        gameUseCases.check(tableId, playerId)
        expectedEvents += PlayerChecked(playerId)
    }


    // TODO fold, raise, all-in, etc
}