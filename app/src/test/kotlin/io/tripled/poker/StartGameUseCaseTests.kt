package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.*
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StartGameUseCaseTests {

    private val eventStore = DummyEventStore()
    private val deck = DummyDeck()
    private val tableService = TableUseCases(eventStore, { deck })

    @Test
    internal fun `start game with two players`() {
        addPlayers()
        deck.queue.addAll(DeckMother().deckOfHearts())

        tableService.startGame()

        expect(eventStore.events).contains.inOrder.only.values(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                GameStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to Hand(Card(Suit.HEART, Value.TEN), Card(Suit.HEART, Value.ACE)),
                        "Jef" to Hand(Card(Suit.HEART, Value.KING), Card(Suit.HEART, Value.QUEEN))
                )),
                FlopIsTurned(
                        Card(Suit.HEART, Value.NINE),
                        Card(Suit.HEART, Value.EIGHT),
                        Card(Suit.HEART, Value.SEVEN)
                ),
                TurnIsTurned(
                        Card(Suit.HEART, Value.SIX)
                ),
                RiverIsTurned(
                        Card(Suit.HEART, Value.FIVE)
                ),
                PlayerWonGame("Jef")
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

        tableService.startGame()

        Assertions.assertFalse(eventStoreContains(GameStarted()))
    }

    private fun eventStoreContains(element: Event) = eventStore.contains(element)

}