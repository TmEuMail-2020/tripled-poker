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
import org.junit.jupiter.api.Test

class StartGameUseCaseTests {

    private val eventStore = DummyEventStore()
    private val deck = DummyDeck()
    private val tableService = TableUseCases(eventStore, { deck })

    @Test
    internal fun `play game with two players`() {
        addPlayers()
        deck.queue.addAll(DeckMother().deckOfHearts())

        tableService.startGame()

        expect(eventStore.events).contains.inOrder.only.values(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"),
                GameStarted(),
                CardsAreDealt(mapOf(
                        "Joe" to Hand(TEN of HEART, ACE of HEART),
                        "Jef" to Hand(KING of HEART, QUEEN of HEART)
                )),
                PlayerCalled("Joe"),
                PlayerCalled("Jef"),
                FlopIsTurned(
                        NINE of HEART,
                        EIGHT of HEART,
                        SEVEN of HEART
                ),
                PlayerCalled("Joe"),
                PlayerCalled("Jef"),
                TurnIsTurned(
                        SIX of HEART
                ),
                PlayerCalled("Joe"),
                PlayerCalled("Jef"),
                RiverIsTurned(
                        FIVE of HEART
                ),
                PlayerCalled("Joe"),
                PlayerCalled("Jef"),
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