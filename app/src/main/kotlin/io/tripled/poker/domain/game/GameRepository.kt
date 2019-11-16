package io.tripled.poker.domain.game

import io.tripled.poker.domain.Event
import io.tripled.poker.vocabulary.GameId

interface GameRepository {
    fun findById(gameId: GameId): Game
    fun save(gameId: GameId, events: List<Event>)
}