package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.*
import io.tripled.poker.domain.*
import io.tripled.poker.domain.Card
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TableProjectionTests {

    private val eventStore = TestEventStore()
    private val tableService = TableUseCases(eventStore, TestDeck())


    @Test
    internal fun `a new table has no players`() {
        val table = tableService.getTable("Jef")

        assertEquals(0, table.players.size)
    }

    @Test
    internal fun `a table with players`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"))
        )

        val table = tableService.getTable("Jef")

        assertEquals(listOf(
                Player("Joe"),
                Player("Jef")
        ), table.players)
    }

    @Test
    internal fun `a table with players and I can only see my own cards`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                RoundStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to Card(Suit.DIAMOND, Value.EIGHT),
                        "Jef" to Card(Suit.CLUB, Value.KING)
                )))
        )

        val table = tableService.getTable("Joe")

        assertEquals(listOf(
                Player("Joe", VisibleCards(listOf(io.tripled.poker.api.response.Card(Suit.DIAMOND, Value.EIGHT)))),
                Player("Jef", HiddenCards(1))
        ), table.players)
    }

    @Test
    internal fun `a table with a winner`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                RoundStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to Card(Suit.DIAMOND, Value.EIGHT),
                        "Jef" to Card(Suit.CLUB, Value.KING)
                )),
                PlayerWonRound("Jef")
        )
        )

        val table = tableService.getTable("Joe")

        assertEquals(Player("Jef",
                VisibleCards(listOf(io.tripled.poker.api.response.Card(Suit.CLUB, Value.KING)))),
                table.winner)

    }

}