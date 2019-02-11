package io.tripled.poker.api

import io.tripled.poker.domain.Table
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection

interface TableService {
    fun join(name: String)
    fun startRound()
    fun getTable(): io.tripled.poker.api.response.Table
}

class TableUseCases(private val eventStore: EventStore):TableService{

    override fun join(name: String) {
        eventStore.save(1, Table(eventStore.findById(1)).join(name))
    }

    override fun getTable() = TableProjection(eventStore.findById(1)).table

    override fun startRound() {
        val table = Table(eventStore.findById(1))
        eventStore.save(1, table.startRound())
    }

}