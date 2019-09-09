package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.ActiveGames

interface GameService {
    fun check(player: String)
    fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>, deck: Deck)
//    fun getGame(playerId: PlayerId): io.tripled.poker.api.response.Table
}

class GameUseCases(
        private val eventStore: EventStore,
        private val eventPublisher: EventPublisher,
        private val activeGames: ActiveGames

) : GameService {
    private val tableId: TableId = "1"

    override fun startGame(tableId: TableId, gameId: GameId, players: List<PlayerId>, deck: Deck){
        projectCurrentlyActiveGame(tableId, gameId)
        executeOnGame(tableId) { start(players, deck) }
    }

    private fun projectCurrentlyActiveGame(tableId: TableId, gameId: GameId) {
        activeGames.save(tableId, gameId)
    }

    override fun check(player: PlayerId)
            = executeOnGame(tableId) { check(player) }

    private fun executeOnGame(tableId: TableId, command: Game.() -> List<Event>) {
        val gameId = activeGames.activeGame(tableId)
        val events = withGame(gameId).command()
        save(gameId, events)
        publish(gameId, events)
    }

    private fun withGame(gameId: GameId) = Game(
            GameState.of(eventStore.findById(gameId) +
            eventStore.findById("1").filterIsInstance<GameStarted>().filter { gs -> gs.gameId == gameId })
    )

    private fun publish(gameId: GameId, events: List<Event>) {
        eventPublisher.publish(gameId, events)
    }

    private fun save(gameId: GameId, events: List<Event>) {
        eventStore.save(gameId, events)
    }
}