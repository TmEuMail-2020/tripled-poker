package io.tripled.poker.api

import io.tripled.poker.domain.Table
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection

class TableService(private val eventStore: EventStore) {

    fun join(name: String) {
        eventStore.save(1, Table(eventStore.findById(1)).join(name))
    }

    fun getTable() = TableProjection(eventStore.findById(1)).table

    fun startRound() {
        val table = Table(eventStore.findById(1))
        eventStore.save(1, table.startRound())
    }

}