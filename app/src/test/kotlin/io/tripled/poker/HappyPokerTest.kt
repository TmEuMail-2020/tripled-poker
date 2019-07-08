package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.contains
import ch.tutteli.atrium.api.cc.en_GB.inOrder
import ch.tutteli.atrium.api.cc.en_GB.only
import ch.tutteli.atrium.api.cc.en_GB.values
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value
import io.tripled.poker.domain.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HappyPokerTest {
    private val eventStore = DummyEventStore()
    private val deck = PredeterminedCardDeck(listOf())
    private val gameUseCases: GameService = GameUseCases(eventStore,{deck})
    private val useCases: TableService = TableUseCases(eventStore, gameUseCases)

    @BeforeEach
    internal fun setUp() {
        deck.queue.clear()
    }

    private fun initDeck() {
        deck.queue.addAll(DeckMother().deckOfHearts())
    }

    @Test
    internal fun `test all usecases and events to play game with two players`() {
        initDeck()

        useCases.join("Joe")
        useCases.join("Jef")
        useCases.startGame()
        allPlayersCheck()
        allPlayersCheck()
        allPlayersCheck()
        allPlayersCheck()

        expect(eventStore.newEvents).contains.inOrder.only.values(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                GameStarted(listOf("Joe", "Jef")),
                HandsAreDealt(DeckMother().deckOfHearts(), mapOf(
                        "Joe" to Hand(Value.TEN of Suit.HEART, Value.ACE of Suit.HEART),
                        "Jef" to Hand(Value.KING of Suit.HEART, Value.QUEEN of Suit.HEART)
                )),
                PlayerChecked("Joe"),
                PlayerChecked("Jef"),
                RoundCompleted(),
                FlopIsTurned(
                        Value.NINE of Suit.HEART,
                        Value.EIGHT of Suit.HEART,
                        Value.SEVEN of Suit.HEART
                ),
                PlayerChecked("Joe"),
                PlayerChecked("Jef"),
                RoundCompleted(),
                TurnIsTurned(
                        Value.SIX of Suit.HEART
                ),
                PlayerChecked("Joe"),
                PlayerChecked("Jef"),
                RoundCompleted(),
                RiverIsTurned(
                        Value.FIVE of Suit.HEART
                ),
                PlayerChecked("Joe"),
                PlayerChecked("Jef"),
                RoundCompleted(),
                PlayerWonGame("Jef")
        )
    }

    private fun allPlayersCheck() {
        gameUseCases.check("Joe")
        gameUseCases.check("Jef")
    }


}