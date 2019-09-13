package io.tripled.poker.dsl

import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.Table
import io.tripled.poker.domain.GameId
import io.tripled.poker.domain.PlayerId

internal class DummyTableUseCases : TableService {
    override fun join(name: String) = Unit
    override fun createGame(): GameId = "gameId"
    override fun getTable(playerId: PlayerId): Table = null!!
}