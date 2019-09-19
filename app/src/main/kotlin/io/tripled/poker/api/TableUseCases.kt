package io.tripled.poker.api

import io.tripled.poker.domain.Event
import io.tripled.poker.domain.Table
import io.tripled.poker.domain.TableState
import io.tripled.poker.domain.Users
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.TableId

interface TableService {
    fun join()
    fun createGame()
    fun getTable(): io.tripled.poker.api.response.Table
}

class TableUseCases(
        private val eventStore: EventStore,
        private val eventPublisher: EventPublisher,
        private val users: Users,
        private val gameIdGenerator: () -> GameId
) : TableService {
    private val tableId: TableId = "1"

    override fun join() {
        executeOnTable { join(users.currentUser.playerId) }
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

    override fun getTable() = TableProjection().table(users.currentUser.playerId, eventStore)

}