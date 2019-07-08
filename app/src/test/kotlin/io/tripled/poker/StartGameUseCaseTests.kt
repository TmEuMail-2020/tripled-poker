package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.*
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit.HEART
import io.tripled.poker.api.response.Value.*
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StartGameUseCaseTests {

    private val eventStore = DummyEventStore()
    private val deck = PredeterminedCardDeck(listOf())
    private val useCases: TableService = TableUseCases(eventStore, { deck })
    private val gameUseCases: GameService = GameUseCases(eventStore)

    @BeforeEach
    internal fun setUp() {
        deck.queue.clear()
    }


    @Test
    internal fun `can't keep playing the game when it's done`() {
        addPlayers()
        initDeck()

        useCases.startGame()

        // TODO: refactor to separate tests
        allPlayersCheck()
        allPlayersCheck()
        allPlayersCheck()
        allPlayersCheck()

        expect {
            // -> execute action on done game
            gameUseCases.check("Joe")
        }.toThrow<RuntimeException>{
            message { startsWith("t'is gedaan, zet u derover") }
        }
    }


    @Test
    internal fun `Start game with two players`() {
        addPlayers()
        initDeck()

        useCases.startGame()

        expect(eventStore.newEvents).contains.inOrder.only.values(
                GameStarted(listOf("Joe", "Jef"), DeckMother().deckOfHearts()),
                HandsAreDealt(mapOf(
                        "Joe" to Hand(TEN of HEART, ACE of HEART),
                        "Jef" to Hand(KING of HEART, QUEEN of HEART)
                ))
        )
    }

    private fun initDeck() {
        deck.queue.addAll(DeckMother().deckOfHearts())
    }

    private fun allPlayersCheck() {
        gameUseCases.check("Joe")
        gameUseCases.check("Jef")
    }

    private fun addPlayers() {
        eventStore.given = listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef")
        )
    }

    @Test
    internal fun `cannot start game with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        useCases.startGame()

        Assertions.assertFalse(eventStoreContains(GameStarted(listOf(), listOf())))
    }

    private fun eventStoreContains(element: Event) = eventStore.contains(element)

}