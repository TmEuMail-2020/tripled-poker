package io.tripled.poker.domain

import ch.tutteli.atrium.api.cc.en_GB.*
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.dsl.AssumeUser
import io.tripled.poker.dsl.DummyEventPublisher
import io.tripled.poker.dsl.DummyEventStore
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.projection.ActiveGames
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.TableId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameIdActiveGame : ActiveGames {
    override fun save(tableId: TableId, gameId: GameId) {}

    override fun activeGame(tableId: TableId) = "gameId"

}

class HappyFoldTest {
    private lateinit var preFoldingEvents: List<Event>
    private val eventStore = DummyEventStore()
    private var deck = PredeterminedCardDeck(listOf())
    private val eventPublisher: EventPublisher = DummyEventPublisher()
    private val assumeUser: AssumeUser = AssumeUser()
    private val activeGames: ActiveGames = GameIdActiveGame()
    private val gameUseCases: GameService = GameUseCases(eventStore, eventPublisher, activeGames, assumeUser) { deck }

    @BeforeEach
    internal fun setUp() {
        deck = PredeterminedCardDeck(DeckMother().fullDeck())
        gameUseCases.startGame("1", "gameId", listOf("Joe", "Jef"))
        preFoldingEvents = eventStore.newEvents.map { t -> t }.toList()
    }

    @Test
    internal fun `folding works`() {
        joeFolds()

        expect(eventStore.newEvents - preFoldingEvents).contains.inOrder.only.value(PlayerFolded("Joe"))
    }

    @Test
    internal fun `folding twice fails`() {
        joeFolds()

        expect { joeFolds() }
                .toThrow<RuntimeException> { }
    }

    private fun joeFolds() {
        assumeUser.assumedPlayerId = "Joe"
        gameUseCases.fold("1")
    }

}