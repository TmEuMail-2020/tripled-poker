package io.tripled.poker.dsl

import io.tripled.poker.domain.GameId
import io.tripled.poker.domain.TableId
import io.tripled.poker.projection.ActiveGames

internal class DummyActiveGames : ActiveGames {
    private val activeGames = HashMap<TableId, GameId>()
    override fun activeGame(tableId: TableId): GameId = activeGames[tableId]!!

    override fun save(tableId: TableId, gameId: GameId) {
        activeGames[tableId] = gameId
    }
}