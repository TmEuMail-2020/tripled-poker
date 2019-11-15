package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.projection.ActiveGames
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

interface GameService {
    fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>)
    fun check(tableId: TableId)
    fun fold(tableId: TableId)
}

class GameUseCases(
        private val gameRepository: GameRepository,
        private val eventPublisher: EventPublisher,
        private val activeGames: ActiveGames,
        private val users: Users,
        private val deckFactory: () -> Deck
) : GameService {

    override fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>) {
        projectCurrentlyActiveGame(tableId, gameId)
        executeOnGame(tableId) { start(players, deckFactory()) }
    }

    private fun projectCurrentlyActiveGame(tableId: TableId, gameId: GameId) = activeGames.save(tableId, gameId)

    override fun check(tableId: TableId) = executeOnGame(tableId) { check(users.currentUser.playerId) }

    override fun fold(tableId: TableId) = executeOnGame(tableId) {
        fold(users.currentUser.playerId)
    }

    private fun executeOnGame(tableId: TableId, command: Game.() -> List<Event>) {
        val gameId = activeGames.activeGame(tableId)
        val events = withGame(gameId).command()
        save(gameId, events)
        publish(gameId, events)
    }

    private fun withGame(gameId: GameId) = gameRepository.findById(gameId)

    private fun publish(gameId: GameId, events: List<Event>) {
        eventPublisher.publish(gameId, events)
    }

    private fun save(gameId: GameId, events: List<Event>) {
        gameRepository.save(gameId, events)
    }
}