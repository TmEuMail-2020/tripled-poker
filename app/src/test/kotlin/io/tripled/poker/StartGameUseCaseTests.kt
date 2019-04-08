package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.contains
import ch.tutteli.atrium.api.cc.en_GB.inOrder
import ch.tutteli.atrium.api.cc.en_GB.only
import ch.tutteli.atrium.api.cc.en_GB.values
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value
import io.tripled.poker.domain.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StartGameUseCaseTests {

    private val eventStore = DummyEventStore()
    private val deck = DummyDeck()
    private val tableUseCases = TableUseCases(eventStore, { deck })

    @AfterEach
    internal fun breakDown() {
        eventStore.reset()
    }

    @Test
    internal fun `play game with two players`() {
        addPlayers()
        deck.queue.addAll(DeckMother().deckOfHearts())

        tableUseCases.startGame()

        expect(eventStore.events).contains.inOrder.only.values(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                GameStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to Hand(Card(Suit.HEART, Value.TEN), Card(Suit.HEART, Value.ACE)),
                        "Jef" to Hand(Card(Suit.HEART, Value.KING), Card(Suit.HEART, Value.QUEEN))
                )),
                PlayerCalled("Joe"),
                PlayerCalled("Jef"),
                FlopIsTurned(
                        Card(Suit.HEART, Value.NINE),
                        Card(Suit.HEART, Value.EIGHT),
                        Card(Suit.HEART, Value.SEVEN)
                ),
                PlayerCalled("Joe"),
                PlayerCalled("Jef"),
                TurnIsTurned(
                        Card(Suit.HEART, Value.SIX)
                ),
                PlayerCalled("Joe"),
                PlayerCalled("Jef"),
                RiverIsTurned(
                        Card(Suit.HEART, Value.FIVE)
                ),
                PlayerCalled("Joe"),
                PlayerCalled("Jef"),
                PlayerWonGame("Jef")
        )
    }

    @Test
    internal fun `player can call in betting round`() {
        eventStore.init(listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                GameStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to Hand(Card(Suit.HEART, Value.TEN), Card(Suit.HEART, Value.ACE)),
                        "Jef" to Hand(Card(Suit.HEART, Value.KING), Card(Suit.HEART, Value.QUEEN))
                )))
        )

        tableUseCases.call("Joe")

        expect(eventStore.eventsAfterInit).contains.inOrder.only.values(
                PlayerCalled("Joe")
        )
    }

    @Test
    internal fun `last player can call in betting round and flop is turned`() {
        eventStore.init(listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                GameStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to Hand(Card(Suit.HEART, Value.TEN), Card(Suit.HEART, Value.ACE)),
                        "Jef" to Hand(Card(Suit.HEART, Value.KING), Card(Suit.HEART, Value.QUEEN))
                )), PlayerCalled("Joe"))
        )

        tableUseCases.call("Jef")

        expect(eventStore.eventsAfterInit).contains.inOrder.only.values(
                PlayerCalled("Jef"),
                FlopIsTurned(
                        Card(Suit.HEART, Value.NINE),
                        Card(Suit.HEART, Value.EIGHT),
                        Card(Suit.HEART, Value.SEVEN)
                )
        )
    }


    private fun addPlayers() {
        eventStore.init(listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"))
        )
    }

    @Test
    internal fun `cannot start game with one player`() {
        eventStore.init(listOf(PlayerJoinedTable("Joe")))

        tableUseCases.startGame()

        Assertions.assertFalse(eventStoreContains(GameStarted()))
    }

    private fun eventStoreContains(element: Event) = eventStore.contains(element)

}