package io.tripled.poker.projection

import io.tripled.poker.domain.GameId
import io.tripled.poker.domain.TableId

interface ActiveGames {
    fun activeGame(tableId: TableId): GameId
    fun save(tableId: TableId, gameId: GameId)
}