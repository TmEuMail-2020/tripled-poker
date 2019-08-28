package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore

interface GameService {
    fun check(gameId: GameId, player: String)
    fun startGame(gameId: GameId, players: List<PlayerId>)
//    fun getGame(playerId: PlayerId): io.tripled.poker.api.response.Table
}

class GameUseCases(
        private val eventStore: EventStore,
        private val deckFactory: () -> Deck,
        private val eventPublisher: EventPublisher
) : GameService {

    override fun startGame(gameId: GameId, players: List<PlayerId>) =
            executeOnGame(gameId) { start(players, deckFactory()) }

    override fun check(gameId: GameId, player: PlayerId)
            = executeOnGame(gameId) { check(player) }

    private fun executeOnGame(gameId: GameId, command: Game.() -> List<Event>) {
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