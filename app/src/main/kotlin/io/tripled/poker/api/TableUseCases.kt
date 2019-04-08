package io.tripled.poker.api

import io.tripled.poker.domain.Deck
import io.tripled.poker.domain.EventPublisher
import io.tripled.poker.domain.Table
import io.tripled.poker.domain.TableState
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection

interface TableService {
    fun join(name: String)
    fun startRound()
    fun getTable(name: String): io.tripled.poker.api.response.Table
}

class TableUseCases(
        private val eventStore: EventStore,
        private val deckFactory: () -> Deck,
        private val eventPublisher: EventPublisher? = null
) : TableService {

    override fun join(name: String) {
        val events = Table(TableState.of( eventStore.findById(1))).join(name)
        eventStore.save(1, events)
        eventPublisher?.publish(1, events)
    }

    override fun getTable(name: String) = TableProjection().table(name, eventStore.findById(1))

    override fun startRound() {
        val tableEvents = eventStore.findById(1)
        val table = Table(TableState.of(tableEvents))

        val outputEvents = table.startRound(deckFactory())

        eventStore.save(1, outputEvents)
        eventPublisher?.publish(1, outputEvents)
    }

}