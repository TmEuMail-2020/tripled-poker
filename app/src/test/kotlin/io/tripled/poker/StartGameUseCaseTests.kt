package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.*
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit.HEARTS
import io.tripled.poker.api.response.Value.*
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StartGameUseCaseTests {

    private val eventStore = TestPokerGame.DummyEventStore()
    private val deck = PredeterminedCardDeck(listOf())
    private val gameUseCases: GameService = GameUseCases(eventStore,{deck})
    private val tableUseCases: TableService = TableUseCases(eventStore, gameUseCases)

    @BeforeEach
    internal fun setUp() {
        deck.queue.clear()
    }


    @Test
    internal fun `can't keep playing the game when it's done`() {
        eventStore.given {
            playersJoin("Joe", "Jef")
        }
        initDeck()

        tableUseCases.startGame()

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
        eventStore.given {
            playersJoin("Joe", "Jef")
        }
        initDeck()

        tableUseCases.startGame()

        expect(eventStore.newEvents).contains.inOrder.only.values(
                GameStarted(listOf("Joe", "Jef")),
                HandsAreDealt(DeckMother().deckOfHearts(), mapOf(
                        "Joe" to Hand(TEN of HEARTS, ACE of HEARTS),
                        "Jef" to Hand(KING of HEARTS, QUEEN of HEARTS)
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

    @Test
    internal fun `cannot start game with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        tableUseCases.startGame()

        Assertions.assertFalse(eventStoreContains(GameStarted(listOf())))
    }

    private fun eventStoreContains(element: Event) = eventStore.contains(element)

}