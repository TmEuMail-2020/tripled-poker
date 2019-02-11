package io.tripled.poker

import io.tripled.poker.api.TableService
import io.tripled.poker.domain.PlayerJoinedTable
import io.tripled.poker.domain.RoundStarted
import io.tripled.poker.eventsourcing.EventStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.Event

class PlayerJoinsTableTests {

    private val eventStore = TestEventStore()
    private val tableService = TableService(eventStore)

    @Test
    internal fun `a player can join the table`() {
        val name = "Joe"

        tableService.join(name)
        val table = tableService.getTable()

        assertEquals(table.players.size, 1)
        assertEquals(table.players.first().name, name)
        assertTrue(eventStore.events.contains(PlayerJoinedTable("Joe")))
    }

    @Test
    internal fun `start round with two players`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"))
        )

        tableService.startRound()

        assertTrue(eventStore.events.contains(RoundStarted()))
    }

    @Test
    internal fun `cannot start round with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        tableService.startRound()

        assertFalse(eventStore.events.contains(RoundStarted()))
    }

    @Test
    internal fun `a new table has no players`() {
        val table = tableService.getTable()

        assertEquals(table.players.size, 0)
    }

}

class TestEventStore(val events: MutableList<Any> = mutableListOf()) : EventStore {
    override fun save(id: Any, events: List<Any>) {
        this.events.addAll(events)
    }


    override fun findById(id: Any): List<Any> {
        return events
    }


}
