package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class TableTests {

    private val eventStore = TestEventStore()
    private val deck = TestDeck()
    private val tableService = TableUseCases(eventStore, {deck})

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
        deck.queue += Card(Suit.HEART, Value.TEN)
        deck.queue += Card(Suit.HEART, Value.ACE)

        tableService.startRound()

        assertTrue(eventStore.events.contains(RoundStarted()))
        assertTrue(eventStore.events.contains(CardsAreDealt(mapOf(
                "Joe" to Card(Suit.HEART, Value.TEN),
                "Jef" to Card(Suit.HEART, Value.ACE)
        ))))
        assertTrue(eventStore.events.contains(PlayerWonRound("Jef")))
    }

    @Test
    internal fun `cannot start round with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        tableService.startRound()

        assertFalse(eventStore.events.contains(RoundStarted()))
    }


}

class TestDeck : Deck {

    val queue = LinkedList<Card>()

    override fun dealCard() = queue.pop()
}
