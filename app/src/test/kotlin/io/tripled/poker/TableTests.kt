package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TableTests {

    private val eventStore = TestEventStore()
    private val tableService = TableUseCases(eventStore)

    @Test
    internal fun `a player can join the table`() {

        tableService.join("Joe")

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
        assertTrue(eventStore.events.contains(CardsAreDealt(mapOf(
                "Joe" to Card(Suit.HEART, Value.ACE),
                "Jef" to Card(Suit.HEART, Value.ACE)
        ))))
        assertTrue(eventStore.events.contains(PlayerWonRound("Joe")))
    }

    @Test
    internal fun `cannot start round with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        tableService.startRound()

        assertFalse(eventStore.events.contains(RoundStarted()))
    }
}
