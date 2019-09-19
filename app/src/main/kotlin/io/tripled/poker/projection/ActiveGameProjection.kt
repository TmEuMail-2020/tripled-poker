package io.tripled.poker.projection

import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.TableId

interface ActiveGames {
    fun activeGame(tableId: TableId): GameId
    fun save(tableId: TableId, gameId: GameId)
}