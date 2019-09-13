package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection

interface TableService {
    fun join(name: String)
    fun createGame()
    fun getTable(playerId: PlayerId): io.tripled.poker.api.response.Table
}

class TableUseCases(
        private val eventStore: EventStore,
        private val gameUseCases: GameService,
        private val eventPublisher: EventPublisher,
        private val gameIdGenerator: () -> GameId
) : TableService {
    private val tableId: TableId = "1"

    override fun join(name: String) {
        executeOnTable { join(name) }
    }

    override fun createGame() = executeOnTable { createGame(gameIdGenerator()) }

    private fun executeOnTable(command: Table.() -> List<Event>) {
        val events = withTable().command()
        save(events)
        publish(events)
    }

    private fun withTable() = Table(TableState.of(eventStore.findById(tableId)))

    private fun publish(events: List<Event>) {
        eventPublisher.publish(tableId, events)
    }

    private fun save(events: List<Event>) {
        eventStore.save(tableId, events)
    }

    override fun getTable(playerId: PlayerId) = TableProjection().table(playerId, eventStore)

}