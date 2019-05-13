package io.tripled.poker.api

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.TableProjection
import sun.audio.AudioPlayer.player

interface TableService {
    fun join(name: String)
    fun startGame()
    fun getTable(name: String): io.tripled.poker.api.response.Table
    fun check(player: String)
    fun flop()
    fun turn()
    fun river()
    fun determineWinner()
}

class TableUseCases(
        private val eventStore: EventStore,
        private val deckFactory: () -> Deck,
        private val eventPublisher: EventPublisher? = null
) : TableService {
    /**COMMAND**/

    override fun join(name: String) = executeOnTable { join(name) }

    override fun startGame() =executeOnTable {startGame(deckFactory()) }

    override fun check(player: String) = executeOnTable { check(player) }

    override fun turn() = executeOnTable { turn() }

    override fun flop() = executeOnTable { flop() }

    override fun river() = executeOnTable { river() }

    override fun determineWinner() = executeOnTable { determineWinner() }

    private fun executeOnTable(command: Table.() -> List<Event>) {
        val events = withTable().command()
        save(events)
        publish(events)
    }

    private fun withTable() = Table(TableState.of(eventStore.findById(1)))

    private fun publish(events: List<Event>) {
        eventPublisher?.publish(1, events)
    }

    private fun save(events: List<Event>) {
        eventStore.save(1, events)
    }

    /*QUERY**/
    override fun getTable(name: String) = TableProjection().table(name, eventStore.findById(1))

}