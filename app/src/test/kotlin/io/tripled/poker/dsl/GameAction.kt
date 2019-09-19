package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.domain.*
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

class GameAction(private val tableId: TableId,
                 private val gameUseCases: GameService,
                 private val assumeUser: AssumeUser,
                 val expectedEvents: ArrayList<Event> = ArrayList()) {

    fun PlayerId.checks() = check(this)

    private fun check(playerId: PlayerId) {
        assumeUser.assumedPlayerId = playerId
        gameUseCases.check(tableId)
        expectedEvents += PlayerChecked(playerId)
    }


    // TODO fold, raise, all-in, etc
}