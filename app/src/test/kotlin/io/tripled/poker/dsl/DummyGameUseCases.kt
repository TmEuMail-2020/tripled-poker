package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.domain.GameId
import io.tripled.poker.domain.PlayerId
import io.tripled.poker.domain.TableId

internal class DummyGameUseCases : GameService {
    override fun check(table:TableId,player: PlayerId) = Unit
    override fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>) = Unit
}