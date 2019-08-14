package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.HiddenCards
import io.tripled.poker.api.response.Player
import io.tripled.poker.api.response.VisibleCards
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetTableUseCasesTest {

    private val eventStore = TestPokerGame.DummyEventStore()
    private var deck = PredeterminedCardDeck(listOf())
    private val tableUseCases = TableUseCases(eventStore, GameUseCases(eventStore,{deck}))

    private val Jef = "Jef"
    private val Joe = "Joe"

    @Test
    internal fun `a new table has no players`() = pokerTest {
        table(Jef){
            assertEquals(0, players.size)
        }
    }

    @Test
    internal fun `a table with players`() = pokerTest {
        given {
            withPlayers(Joe, Jef)
        }

        table(Jef){
            expect(players).toBe(listOf(
                    Player(Joe),
                    Player(Jef)
            ))
        }
    }

    @Test
    internal fun `a table with players and I can only see my own cards`() {
        eventStore.given {
            startGame(deck.cards, Joe to suitedConnectors,
                    Jef to suitedAceKing)
        }

        val table = tableUseCases.getTable(Joe)

        assertEquals(listOf(
                Player(Joe, VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                Player(Jef, HiddenCards(2))
        ), table.players)
    }

    @Test
    internal fun `a table with a winner`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable(Joe),
                PlayerJoinedTable(Jef),
                GameStarted(listOf(Joe, Jef)),
                HandsAreDealt(deck.cards, mapOf(
                        Joe to suitedConnectors,
                        Jef to suitedAceKing
                )),
                PlayerWonGame(Jef)
        )
        )

        val table = tableUseCases.getTable(Joe)

        assertEquals(Player(Jef,
                VisibleCards(suitedAceKing.cards().map { it.mapToCard() })),
                table.winner)

    }

    @Test
    internal fun `all cards are dealt`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable(Joe),
                PlayerJoinedTable(Jef),
                GameStarted(listOf(Joe, Jef)),
                HandsAreDealt(deck.cards, mapOf(
                        Joe to suitedConnectors,
                        Jef to suitedAceKing
                )),
                PlayerWonGame(Jef)
        )
        )

        val table = tableUseCases.getTable(Joe)

        assertEquals(Player(Jef,
                VisibleCards(suitedAceKing.cards().map { it.mapToCard() })),
                table.winner)

    }

    @Test
    internal fun `new deck is created between games`() {
        val tableService = TableUseCases(eventStore, GameUseCases(eventStore,{ShuffledDeck()}))

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


        tableService.startGame()
        tableService.startGame()
        tableService.startGame()
        tableService.startGame()
        tableService.startGame()
        tableService.startGame()
        tableService.startGame()
    }

    @Test
    internal fun `player joins after cards are dealt`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable(Joe),
                GameStarted(listOf(Joe, Jef)),
                HandsAreDealt(deck.cards, mapOf(
                        Joe to suitedConnectors
                )),
                PlayerJoinedTable(Jef)
        ))

        val table = tableUseCases.getTable(Joe)

        assertEquals(listOf(
                Player(Joe, VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                Player(Jef)
        ), table.players)
    }


    @Test
    internal fun `player joins after game started`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable(Joe),
                GameStarted(listOf(Joe, Jef)),
                PlayerJoinedTable(Jef),
                HandsAreDealt(deck.cards, mapOf(
                        Joe to suitedConnectors
                ))
        ))

        val table = tableUseCases.getTable(Joe)

        assertEquals(listOf(
                Player(Joe, VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                Player(Jef)
        ), table.players)
    }
}