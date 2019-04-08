package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class JoinTableTests {

    private val eventStore = DummyEventStore()
    private val deck = TestDeck()
    private val tableService = TableUseCases(eventStore, {deck})

    @Test
    internal fun `a player can join the table`() {

        tableService.join("Joe")

        assertTrue(eventStore.contains(PlayerJoinedTable("Joe")))
    }

}

class TestDeck : Deck {

    val queue = LinkedList<Card>()

    override fun dealCard() = queue.pop()
}
