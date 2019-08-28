package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection

interface TableService {
    fun join(name: String)
    fun startGame(): GameId
    fun getTable(playerId: PlayerId): io.tripled.poker.api.response.Table
}

class TableUseCases(
        private val eventStore: EventStore,
        private val gameUseCases: GameService,
        private val eventPublisher: EventPublisher,
        private val gameIdGenerator: () -> GameId
) : TableService {
    /** COMMAND **/

    override fun join(name: String) {
        executeOnTable { join(name) }
    }

    override fun startGame(): GameId {
        val events = executeOnTable { startGame(gameIdGenerator()) }
        val gameStartedEvent = events.lastEventOrNull<GameStarted>()

        gameStartedEvent?.apply {
            gameUseCases.startGame(this.gameId, this.players)
        }

        return gameStartedEvent!!.gameId
    }

    private fun executeOnTable(command: Table.() -> List<Event>): List<Event> {
        val events = withTable().command()
        save(events)
        publish(events)
        return events
    }

    private fun withTable() = Table(TableState.of(eventStore.findById(1)))

    private fun publish(events: List<Event>) {
        eventPublisher.publish(1, events)
    }

    private fun save(events: List<Event>) {
        eventStore.save(1, events)
    }

    /** QUERY **/
    override fun getTable(playerId: PlayerId) = TableProjection().table(playerId, eventStore)

}