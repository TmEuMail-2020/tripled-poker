package io.tripled.poker.api

import io.tripled.poker.domain.Deck
import io.tripled.poker.domain.EventPublisher
import io.tripled.poker.domain.Table
import io.tripled.poker.domain.TableState
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection

interface TableService {
    fun join(name: String)
    fun startGame()
    fun getTable(name: String): io.tripled.poker.api.response.Table
    fun call(player: String)
}

class TableUseCases(
        private val eventStore: EventStore,
        private val deckFactory: () -> Deck,
        private val eventPublisher: EventPublisher? = null
) : TableService {
    /**COMMAND**/

    override fun join(name: String) {
        val events = Table(TableState.of(eventStore.findById(1))).join(name)
        eventStore.save(1, events)
        eventPublisher?.publish(1, events)
    }

    override fun startGame() {
        val tableEvents = eventStore.findById(1)
        val table = Table(TableState.of(tableEvents))

        val outputEvents = table.startGame(deckFactory())

        eventStore.save(1, outputEvents)
        eventPublisher?.publish(1, outputEvents)
    }

    override fun call(player: String) {
        val tableEvents = eventStore.findById(1)
        val table = Table(TableState.of(tableEvents))

        val outputEvents = table.call(player)

        eventStore.save(1, outputEvents)
        eventPublisher?.publish(1, outputEvents)
    }


    /*QUERY**/

    override fun getTable(name: String) = TableProjection().table(name, eventStore.findById(1))

}