package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StartRoundUseCaseTests {
    private val eventStore = DummyEventStore()
    private val deck = TestDeck()
    private val tableService = TableUseCases(eventStore, { deck })

    @Test
    internal fun `start round with two players`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"))
        )
        deck.queue += Card(Suit.HEART, Value.TEN)
        deck.queue += Card(Suit.HEART, Value.ACE)
        deck.queue += Card(Suit.HEART, Value.KING)
        deck.queue += Card(Suit.HEART, Value.QUEEN)
        deck.queue += Card(Suit.HEART, Value.NINE)
        deck.queue += Card(Suit.HEART, Value.EIGHT)
        deck.queue += Card(Suit.HEART, Value.SEVEN)
        deck.queue += Card(Suit.HEART, Value.SIX)
        deck.queue += Card(Suit.HEART, Value.FIVE)

        tableService.startRound()

        assertTrue(eventStoreContains(RoundStarted()))
        assertTrue(eventStoreContains(CardsAreDealt(mapOf(
                "Joe" to Hand(Card(Suit.HEART, Value.TEN), Card(Suit.HEART, Value.ACE)),
                "Jef" to Hand(Card(Suit.HEART, Value.KING), Card(Suit.HEART, Value.QUEEN))
        ))))
        assertTrue(eventStoreContains(FlopIsTurned(
                Card(Suit.HEART, Value.NINE),
                Card(Suit.HEART, Value.EIGHT),
                Card(Suit.HEART, Value.SEVEN)
        )))
        assertTrue(eventStoreContains(TurnIsTurned(
                Card(Suit.HEART, Value.SIX)
        )))
        assertTrue(eventStoreContains(RiverIsTurned(
                Card(Suit.HEART, Value.FIVE)
        )))
        assertTrue(eventStoreContains(PlayerWonRound("Jef")))
    }

    @Test
    internal fun `cannot start round with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        tableService.startRound()

        Assertions.assertFalse(eventStoreContains(RoundStarted()))
    }

    private fun eventStoreContains(element: Event) = eventStore.contains(element)

}