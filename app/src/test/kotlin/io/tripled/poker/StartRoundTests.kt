package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StartRoundTests {
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

        tableService.startRound()

        Assertions.assertTrue(eventStore.events.contains(RoundStarted()))
        Assertions.assertTrue(eventStore.events.contains(CardsAreDealt(mapOf(
                "Joe" to Hand(Card(Suit.HEART, Value.TEN), Card(Suit.HEART, Value.ACE)),
                "Jef" to Hand(Card(Suit.HEART, Value.KING), Card(Suit.HEART, Value.QUEEN))
        ))))
        Assertions.assertTrue(eventStore.events.contains(PlayerWonRound("Jef")))
    }

    @Test
    internal fun `cannot start round with one player`() {
        eventStore.save(1, listOf(PlayerJoinedTable("Joe")))

        tableService.startRound()

        Assertions.assertFalse(eventStore.events.contains(RoundStarted()))
    }

}