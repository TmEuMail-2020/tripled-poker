package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JoinTableTests {

    private val eventStore = DummyEventStore()
    private val deck = PredeterminedCardDeck(listOf())
    private val tableService = TableUseCases(eventStore, {deck})

    @Test
    internal fun `a player can join the table`() {

        tableService.join("Joe")

        assertTrue(eventStore.contains(PlayerJoinedTable("Joe")))
    }

}

