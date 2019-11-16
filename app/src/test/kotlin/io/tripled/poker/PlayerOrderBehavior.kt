package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.message
import ch.tutteli.atrium.api.cc.en_GB.startsWith
import ch.tutteli.atrium.api.cc.en_GB.toThrow
import ch.tutteli.atrium.creating.Assert
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.app.GameUseCases
import io.tripled.poker.app.api.GameService
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.Event
import io.tripled.poker.domain.GameIdActiveGame
import io.tripled.poker.domain.cards.PredeterminedCardDeck
import io.tripled.poker.dsl.AssumeUser
import io.tripled.poker.dsl.DummyEventPublisher
import io.tripled.poker.dsl.DummyEventStore
import io.tripled.poker.dsl.DummyGameRepository
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.projection.ActiveGames
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerOrderBehavior {
    private lateinit var preFoldingEvents: List<Event>
    private val eventStore = DummyEventStore()
    private var deck = PredeterminedCardDeck(listOf())
    private val eventPublisher: EventPublisher = DummyEventPublisher()
    private val assumeUser: AssumeUser = AssumeUser()
    private val activeGames: ActiveGames = GameIdActiveGame()
    private val gameUseCases: GameService = GameUseCases(DummyGameRepository(eventStore), eventPublisher, activeGames, assumeUser) { deck }

    @BeforeEach
    internal fun setUp() {
        deck = PredeterminedCardDeck(DeckMother().fullDeck())
        gameUseCases.startGame("1", "gameId", listOf("Joe", "Jef", "Olivier"))
        preFoldingEvents = eventStore.newEvents.map { t -> t }.toList()
    }

    @Test
    internal fun `cant check out of order`() {
        expect {
            jefChecks()
        }.toThrow<RuntimeException> {
            assertMessage()
        }
    }

    @Test
    internal fun `can check in order`() {
        joeChecks()
    }

    @Test
    internal fun `cant fold out of order`() {
        expect {
            jefFolds()
        }.toThrow<RuntimeException> {
            assertMessage()
        }
    }

    @Test
    internal fun `can fold in order`() {
        joeFolds()
    }

    private fun Assert<RuntimeException>.assertMessage() {
        message { startsWith("t'is nie oan aaa e") }
    }

    private fun jefChecks() {
        assumeUser.assumedPlayerId = "Jef"
        gameUseCases.check("1")
    }

    private fun joeChecks() {
        assumeUser.assumedPlayerId = "Joe"
        gameUseCases.check("1")
    }

    private fun jefFolds() {
        assumeUser.assumedPlayerId = "Jef"
        gameUseCases.fold("1")
    }

    private fun joeFolds() {
        assumeUser.assumedPlayerId = "Joe"
        gameUseCases.fold("1")
    }
}