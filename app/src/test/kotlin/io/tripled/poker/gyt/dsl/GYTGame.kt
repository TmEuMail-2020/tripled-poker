package io.tripled.poker.gyt.dsl

import ch.tutteli.atrium.api.cc.en_GB.isNotEmpty
import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.*
import io.tripled.poker.dsl.*
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.projection.ActiveGames
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

fun pokerGameScenario(test: GYTTestPokerGame.() -> Unit): Unit {
    GYTTestPokerGame()
            .apply(test)
}


open class GYTTestPokerGame(private val deck: PredeterminedCardTestDeck = PredeterminedCardTestDeck(DeckMother().deckOfHearts()),
                            private val eventStore: DummyEventStore = DummyEventStore(),
                            private val eventPublisher: EventPublisher = DummyEventPublisher(),
                            private val assumeUser: AssumeUser = AssumeUser(),
                            private val gameId: GameId = "gameId",
                            private val tableId: TableId = "1",//Break me TODO
                            private val activeGames: ActiveGames = DummyActiveGames(),
                            private val tableUseCases: TableService = TableUseCases(eventStore, eventPublisher, assumeUser) { gameId },
                            private val gameUseCases: GameService = GameUseCases(eventStore, eventPublisher, activeGames, assumeUser) { deck }) {

    //Test flow
    fun given(function: Given.() -> Unit) {
        Given().function()
    }

    fun action(function: Action.() -> Unit) {
        Action().function()
    }

    fun verify(function: Verify.() -> Unit) {
        Verify().function()
    }

    inner class Given {
        fun playerIsPresent(playerId: PlayerId) {
            appendTableEvent(PlayerJoinedTable(playerId))
        }

        fun playerHasChecked(playerId: PlayerId) {
            appendTableEvent(PlayerChecked(playerId))
        }

        fun gameHasStarted(players: List<PlayerId>) {
            appendTableEvent(GameCreated(gameId, players))
            appendGameEvent(GameStarted(players, deck.cards))
            //Close your eyes. This projection is normally triggered by StartGameUsecase
            activeGames.save(tableId, gameId)
            //Bug volgens Guido
            val testDeck = PredeterminedCardTestDeck(DeckMother().deckOfHearts())
            val hands = players.associateWith { Hand(testDeck.dealCard(), testDeck.dealCard()) }
            appendGameEvent(HandsAreDealt(hands))

        }

        private fun appendTableEvent(event: Event) {
            eventStore.given.append("1", listOf(event))
        }

        private fun appendGameEvent(event: Event) {
            eventStore.given.append(gameId, listOf(event))
        }
    }

    inner class Action {
        internal fun playerJoins(playerName: String) {
            setCurrentPlayer(playerName)
            tableUseCases.join()
        }

        private fun setCurrentPlayer(playerName: String) {
            assumeUser.assumedPlayerId = playerName
        }

        fun startGame(players: List<PlayerId>) {
            tableUseCases.createGame()
            gameUseCases.startGame("1", gameId, players)
        }

        fun playerChecks(playerName: PlayerId) {
            setCurrentPlayer(playerName)
            gameUseCases.check(tableId)
        }
    }

    inner class Verify {
        internal fun playerJoined(playerName: String) {
            verifyEventIsPresent(PlayerJoinedTable(playerName))
        }

        fun gameHasBeenCreated(players: List<PlayerId>) {
            verifyEventIsPresent(GameCreated(gameId, players))
        }

        fun gameHasStarted(players: List<PlayerId>, cards: List<Card>) {
            verifyEventIsPresent(GameStarted(players, cards))
        }

        fun gameHasStarted(players: List<PlayerId>) {
            verifyEventOfTypeIsPresent<GameStarted>()
        }

        fun handsAreDealt() {
            verifyEventOfTypeIsPresent<HandsAreDealt>()
        }

        fun playerHasChecked(playerId: PlayerId) {
            verifyEventIsPresent(PlayerChecked(playerId))
        }

        private fun verifyEventIsPresent(event: Event) {
            expect(eventStore.newEvents.contains(event)).toBe(true)
        }

        private inline fun <reified T> verifyEventOfTypeIsPresent() {
            val eventsThatMatch = eventStore.newEvents.filterIsInstance<T>()
            expect(eventsThatMatch).isNotEmpty()
        }
    }
}