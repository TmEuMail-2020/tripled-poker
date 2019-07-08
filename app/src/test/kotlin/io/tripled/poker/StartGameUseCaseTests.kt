package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.contains
import ch.tutteli.atrium.api.cc.en_GB.inOrder
import ch.tutteli.atrium.api.cc.en_GB.only
import ch.tutteli.atrium.api.cc.en_GB.values
import ch.tutteli.atrium.verbs.expect
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
    private val useCases = TableUseCases(eventStore, { deck })

    @BeforeEach
    internal fun setUp() {
        deck.queue.clear()
    }

    @Test
    internal fun `play game with two players`() {
        addPlayers()
        deck.queue.addAll(DeckMother().deckOfHearts())

        useCases.startGame()
        useCases.check("Joe")
        useCases.check("Jef")

        useCases.check("Joe")
        useCases.check("Jef")

        useCases.check("Joe")
        useCases.check("Jef")

        useCases.river()
        useCases.check("Joe")
        useCases.check("Jef")

        useCases.determineWinner()

        expect(eventStore.events).contains.inOrder.only.values(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                GameStarted(DeckMother().deckOfHearts()),
                HandsAreDealt(mapOf(
                        "Joe" to Hand(TEN of HEART, ACE of HEART),
                        "Jef" to Hand(KING of HEART, QUEEN of HEART)
                )),
                PlayerChecked("Joe"),
                PlayerChecked("Jef"),
                RoundCompleted(),
                FlopIsTurned(
                        NINE of HEART,
                        EIGHT of HEART,
                        SEVEN of HEART
                ),
                PlayerChecked("Joe"),
                PlayerChecked("Jef"),
                RoundCompleted(),
                TurnIsTurned(
                        SIX of HEART
                ),
                PlayerChecked("Joe"),
                PlayerChecked("Jef"),
                RoundCompleted(),
                RiverIsTurned(
                        FIVE of HEART
                ),
                PlayerChecked("Joe"),
                PlayerChecked("Jef"),
                RoundCompleted(),
                PlayerWonGame("Jef")
        )
    }

    @Test
    internal fun `Start game with two players`() {
        addPlayers()
        deck.queue.addAll(DeckMother().deckOfHearts())

        useCases.startGame()

        expect(eventStore.events).contains.inOrder.only.values(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                GameStarted(DeckMother().deckOfHearts()),
                HandsAreDealt(mapOf(
                        "Joe" to Hand(TEN of HEART, ACE of HEART),
                        "Jef" to Hand(KING of HEART, QUEEN of HEART)
                ))
        )
    }

    private fun addPlayers() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"))
        )
    }

    @Test
    internal fun `cannot start game with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        useCases.startGame()

        Assertions.assertFalse(eventStoreContains(GameStarted(listOf())))
    }

    private fun eventStoreContains(element: Event) = eventStore.contains(element)

}