package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.isEmpty
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.PlayerJoinedTable
import io.tripled.poker.domain.PredeterminedCardDeck
import io.tripled.poker.domain.filterEvents
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

    @Test
    internal fun `a player can't join the table with an empty name`() {
        tableService.join("")

        assertFalse(eventStore.contains(PlayerJoinedTable("")))
    }

    @Test
    internal fun `a player can't join twice with the same name`() {
        eventStore.given {
            playersJoin("jef")
        }

        tableService.join("jef")

        expect(eventStore.newEvents).isEmpty()
    }

}

