package io.tripled.poker.dsl

import io.tripled.poker.domain.Event
import io.tripled.poker.domain.filterEvents
import io.tripled.poker.domain.table.PlayerJoinedTable
import io.tripled.poker.domain.table.Table
import io.tripled.poker.domain.table.TableRepository
import io.tripled.poker.domain.table.TableState
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.vocabulary.TableId

class DummyTableRepository(private val eventStore: EventStore) : TableRepository {
    override fun findTableById(tableId: TableId) = Table(TableReducer().of(eventStore.findById(tableId)))

    override fun saveTable(tableId: TableId, events: List<Event>)  = eventStore.append(tableId, events)

    internal class TableReducer() {
        fun of(events: List<Event>) = TableState(players(events))

        private fun players(events: List<Event>): List<String> = events
                .filterEvents<PlayerJoinedTable>()
                .map { event -> event.name }
    }

}