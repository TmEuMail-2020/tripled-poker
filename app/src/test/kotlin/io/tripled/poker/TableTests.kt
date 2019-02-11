package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.Card
import io.tripled.poker.domain.CardsAreDealt
import io.tripled.poker.domain.PlayerJoinedTable
import io.tripled.poker.domain.RoundStarted
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
                "Joe" to Card(),
                "Jef" to Card()
        ))))
    }

    @Test
    internal fun `cannot start round with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        tableService.startRound()

        assertFalse(eventStore.events.contains(RoundStarted()))
    }
}

