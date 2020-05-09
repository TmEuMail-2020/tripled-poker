package io.tripled.poker.graphql

import io.tripled.poker.app.api.GameService
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

class DummyGameService : GameService {
    override fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>) {
        TODO("Not yet implemented")
    }

    override fun check(tableId: TableId) {
        TODO("Not yet implemented")
    }

    override fun fold(tableId: TableId) {
        TODO("Not yet implemented")
    }
}