package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection

interface TableService {
    fun join(name: String)
    fun startGame()
    fun getTable(playerId: PlayerId): io.tripled.poker.api.response.Table
}

interface GameService {
    fun check(player: String)
    fun startGame(players: List<PlayerId>)
//    fun getGame(playerId: PlayerId): io.tripled.poker.api.response.Table
}

class TableUseCases(
        private val eventStore: EventStore,
        private val gameUseCases: GameService,
        private val eventPublisher: EventPublisher? = null
) : TableService {
    /**COMMAND**/

    override fun join(name: String) {
        executeOnTable { join(name) }
    }

    override fun startGame() {
        val events = executeOnTable { startGame() }
        val gameStartedEvent = events.lastEventOrNull<GameStarted>()

        gameStartedEvent?.apply {
            gameUseCases.startGame(this.players)
        }
    }

    private fun executeOnTable(command: Table.() -> List<Event>): List<Event> {
        val events = withTable().command()
        save(events)
        publish(events)
        return events
    }

    private fun withTable() = Table(TableState.of(eventStore.findById(1)))

    private fun publish(events: List<Event>) {
        eventPublisher?.publish(1, events)
    }

    private fun save(events: List<Event>) {
        eventStore.save(1, events)
    }

    /*QUERY**/
    override fun getTable(name: PlayerId) = TableProjection().table(name, eventStore.findById(1))

}

class GameUseCases(
        private val eventStore: EventStore,
        private val deckFactory: () -> Deck,
        private val eventPublisher: EventPublisher? = null
) : GameService {

    override fun startGame(players: List<PlayerId>) {
        executeOnGame { start(players, deckFactory.invoke()) }
    }

    override fun check(player: PlayerId) = executeOnGame { check(player) }

    private fun executeOnGame(command: Game.() -> List<Event>) {
        val events = withGame().command()
        save(events)
        publish(events)
    }

    private fun withGame() = Game(GameState.of(eventStore.findById(1)))

    private fun publish(events: List<Event>) {
        eventPublisher?.publish(1, events)
    }

    private fun save(events: List<Event>) {
        eventStore.save(1, events)
    }
}