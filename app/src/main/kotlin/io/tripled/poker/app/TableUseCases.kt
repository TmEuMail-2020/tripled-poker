package io.tripled.poker.app

import io.tripled.poker.app.api.TableService
import io.tripled.poker.domain.Event
import io.tripled.poker.domain.table.Table
import io.tripled.poker.domain.Users
import io.tripled.poker.domain.table.TableRepository
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.TableId

class TableUseCases(
        private val tableRepository: TableRepository,
        private val eventPublisher: EventPublisher,
        private val users: Users,
        private val gameIdGenerator: () -> GameId
) : TableService {
    private val tableId: TableId = "1"

    override fun join() {
        executeOnTable { join(users.currentUser.playerId) }
    }

    override fun createGame() = executeOnTable { createGame(gameIdGenerator()) }

    override fun getTable() = tableRepository.projectTable(users.currentUser.playerId)

    private fun executeOnTable(command: Table.() -> List<Event>) {
        val events = withTable().command()
        save(events)
        publish(events)
    }

    private fun withTable() = tableRepository.findTableById(tableId)

    private fun publish(events: List<Event>) {
        eventPublisher.publish(tableId, events)
    }

    private fun save(events: List<Event>) {
        tableRepository.saveTable(tableId, events)
    }
}