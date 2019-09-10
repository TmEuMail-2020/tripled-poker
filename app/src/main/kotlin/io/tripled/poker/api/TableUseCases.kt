package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection
import java.lang.RuntimeException

interface TableService {
    fun join(name: String)
    fun startGame(): GameId
    fun getTable(playerId: PlayerId): io.tripled.poker.api.response.Table
}

class TableUseCases(
        private val eventStore: EventStore,
        private val gameUseCases: GameService,
        private val deckFactory: () -> Deck,
        private val eventPublisher: EventPublisher,
        private val gameIdGenerator: () -> GameId
) : TableService {
    private val tableId: TableId = "1"

    /** COMMAND **/
    override fun join(name: String) {
        executeOnTable { join(name) }
    }

    override fun startGame(): GameId {
        val events = executeOnTable { startGame(gameIdGenerator(), deckFactory()) }
        val gameStartedEvent = events.lastEventOrNull<GameStarted>()

        gameStartedEvent?.apply {
            gameUseCases.startGame(tableId, this.gameId, this.players, deckFactory())
            return gameStartedEvent.gameId
        }

        throw RuntimeException("Can't start a game with only 1 player")
    }

    private fun executeOnTable(command: Table.() -> List<Event>): List<Event> {
        val events = withTable().command()
        save(events)
        publish(events)
        return events
    }

    private fun withTable() = Table(TableState.of(eventStore.findById(1)))

    private fun publish(events: List<Event>) {
        eventPublisher.publish(tableId, events)
    }

    private fun save(events: List<Event>) {
        eventStore.save(tableId, events)
    }

    /** QUERY **/
    override fun getTable(playerId: PlayerId) = TableProjection().table(playerId, eventStore)

}