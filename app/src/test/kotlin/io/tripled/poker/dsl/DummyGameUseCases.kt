package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

internal class DummyGameUseCases : GameService {
    override fun check(tableId: TableId, playerId: PlayerId) = Unit
    override fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>) = Unit
}