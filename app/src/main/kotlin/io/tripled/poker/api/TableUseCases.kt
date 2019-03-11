package io.tripled.poker.api

import io.tripled.poker.domain.Deck
import io.tripled.poker.domain.EventPublisher
import io.tripled.poker.domain.Table
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection

interface TableService {
    fun join(name: String)
    fun startRound()
    fun getTable(name: String): io.tripled.poker.api.response.Table
}

class TableUseCases(
        private val eventStore: EventStore,
        private val deck: Deck,
        private val eventPublisher: EventPublisher?=null
        ) : TableService {

    override fun join(name: String) {
        val events = Table(eventStore.findById(1)).join(name)
        eventStore.save(1, events)
        eventPublisher?.publish(1, events)
    }

    override fun getTable(name: String) = TableProjection(name, eventStore.findById(1)).table

    override fun startRound() {
        val events = Table(eventStore.findById(1)).startRound(deck)
        eventStore.save(1, events)
        eventPublisher?.publish(1, events)

    }

}