package io.tripled.poker.app.api

import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

interface GameService {
    fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>)
    fun check(tableId: TableId)
    fun fold(tableId: TableId)
}