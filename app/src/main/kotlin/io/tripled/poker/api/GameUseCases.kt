package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.ActiveGames

interface GameService {
    fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>)
    fun check(tableId: TableId, player: PlayerId)
}

class GameUseCases(
        private val eventStore: EventStore,
        private val eventPublisher: EventPublisher,
        private val activeGames: ActiveGames,
        private val deckFactory: () -> Deck
) : GameService {

    override fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>) {
        projectCurrentlyActiveGame(tableId, gameId)
        executeOnGame(tableId) { start(players, deckFactory()) }
    }

    private fun projectCurrentlyActiveGame(tableId: TableId, gameId: GameId) = activeGames.save(tableId, gameId)

    override fun check(tableId: TableId, player: PlayerId) = executeOnGame(tableId) { check(player) }

    private fun executeOnGame(tableId: TableId, command: Game.() -> List<Event>) {
        val gameId = activeGames.activeGame(tableId)
        val events = withGame(gameId).command()
        save(gameId, events)
        publish(gameId, events)
    }

    private fun withGame(gameId: GameId) = Game(GameState.of(eventStore.findById(gameId)))

    private fun publish(gameId: GameId, events: List<Event>) {
        eventPublisher.publish(gameId, events)
    }

    private fun save(gameId: GameId, events: List<Event>) {
        eventStore.save(gameId, events)
    }
}