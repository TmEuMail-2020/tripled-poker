package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.HiddenCards
import io.tripled.poker.api.response.Player
import io.tripled.poker.api.response.VisibleCards
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test



class GetTableUseCasesTest {

    private val eventStore = DummyEventStore()
    var deck = DummyDeck()
    private val tableService = TableUseCases(eventStore, { deck })

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
                        "Joe" to suitedConnectors,
                        "Jef" to suitedAceKing)
                )))

        val table = tableService.getTable("Joe")

        assertEquals(listOf(
                Player("Joe", VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                Player("Jef", HiddenCards(2))
        ), table.players)
    }

    @Test
    internal fun `a table with a winner`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                RoundStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to suitedConnectors,
                        "Jef" to suitedAceKing
                )),
                PlayerWonRound("Jef")
        )
        )

        val table = tableService.getTable("Joe")

        assertEquals(Player("Jef",
                VisibleCards(suitedAceKing.cards().map { it.mapToCard() })),
                table.winner)

    }

    @Test
    internal fun `all cards are dealt`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                RoundStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to suitedConnectors,
                        "Jef" to suitedAceKing
                )),
                PlayerWonRound("Jef")
        )
        )

        val table = tableService.getTable("Joe")

        assertEquals(Player("Jef",
                VisibleCards(suitedAceKing.cards().map { it.mapToCard() })),
                table.winner)

    }

    @Test
    internal fun `new deck is created between rounds`() {
        val tableService = TableUseCases(eventStore, { ShuffledDeck() })

        eventStore.save(1, listOf(
                PlayerJoinedTable("1"),
                PlayerJoinedTable("2"),
                PlayerJoinedTable("3"),
                PlayerJoinedTable("4"),
                PlayerJoinedTable("5"),
                PlayerJoinedTable("6"),
                PlayerJoinedTable("7"),
                PlayerJoinedTable("8"),
                PlayerJoinedTable("9"),
                PlayerJoinedTable("10")
        ))


        tableService.startRound()
        tableService.startRound()
        tableService.startRound()
        tableService.startRound()
        tableService.startRound()
        tableService.startRound()
        tableService.startRound()
    }

    @Test
    internal fun `player joins after cards are dealt`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                RoundStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to suitedConnectors
                )),
                PlayerJoinedTable("Jef")
        ))

        val table = tableService.getTable("Joe")

        assertEquals(listOf(
                Player("Joe", VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                Player("Jef")
        ), table.players)
    }


    @Test
    internal fun `player joins after round started`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                RoundStarted(),
                PlayerJoinedTable("Jef"),
                CardsAreDealt(mapOf(
                        "Joe" to suitedConnectors
                ))
        ))

        val table = tableService.getTable("Joe")

        assertEquals(listOf(
                Player("Joe", VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                Player("Jef")
        ), table.players)
    }
}