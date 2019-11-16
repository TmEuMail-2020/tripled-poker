package io.tripled.poker.dsl

import io.tripled.poker.app.api.GameService
import io.tripled.poker.domain.Event
import io.tripled.poker.domain.game.PlayerChecked
import io.tripled.poker.domain.game.PlayerFolded
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

class GameAction(private val tableId: TableId,
                 private val gameUseCases: GameService,
                 private val assumeUser: AssumeUser,
                 val expectedEvents: ArrayList<Event> = ArrayList()) {

    fun PlayerId.checks() = checkIt(this)

    fun PlayerId.folds() = foldIt(this)

    private fun checkIt(playerId: PlayerId) {
        assumeUser.assumedPlayerId = playerId
        gameUseCases.check(tableId)
        expectedEvents += PlayerChecked(playerId)
    }

    private fun foldIt(playerId: PlayerId) {
        assumeUser.assumedPlayerId = playerId
        gameUseCases.fold(tableId)
        expectedEvents += PlayerFolded(playerId)
    }


        // TODO fold, raise, all-in, etc
}