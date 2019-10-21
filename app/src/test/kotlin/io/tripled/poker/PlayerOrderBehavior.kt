package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.message
import ch.tutteli.atrium.api.cc.en_GB.startsWith
import ch.tutteli.atrium.api.cc.en_GB.toThrow
import ch.tutteli.atrium.domain.builders.creating.CharSequenceAssertionsBuilder.startsWith
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.Event
import io.tripled.poker.domain.GameIdActiveGame
import io.tripled.poker.domain.PredeterminedCardDeck
import io.tripled.poker.dsl.AssumeUser
import io.tripled.poker.dsl.DummyEventPublisher
import io.tripled.poker.dsl.DummyEventStore
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
    private val gameUseCases: GameService = GameUseCases(eventStore, eventPublisher, activeGames, assumeUser) { deck }

    @BeforeEach
    internal fun setUp() {
        deck = PredeterminedCardDeck(DeckMother().fullDeck())
        gameUseCases.startGame("1", "gameId", listOf("Joe", "Jef", "Olivier"))
        preFoldingEvents = eventStore.newEvents.map { t -> t }.toList()
    }

    @Test
    internal fun `cant play out of order`() {
        expect {
            jefChecks()
        }.toThrow<RuntimeException> {}
    }

    @Test
    internal fun `can play in order`() {
        joeChecks()
    }

    private fun jefChecks() {
        assumeUser.assumedPlayerId = "Jef"
        gameUseCases.check("1")
    }

    private fun joeChecks() {
        assumeUser.assumedPlayerId = "Joe"
        gameUseCases.check("1")
    }
}