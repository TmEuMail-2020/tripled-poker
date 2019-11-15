package io.tripled.poker.domain

import io.tripled.poker.vocabulary.GameId

interface GameRepository {
    fun findById(gameId: GameId): Game
    fun save(gameId: GameId, events: List<Event>)
}