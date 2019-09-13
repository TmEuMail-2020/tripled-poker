package io.tripled.poker.dsl

import ch.tutteli.atrium.api.cc.en_GB.hasSize
import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.*
import io.tripled.poker.eventpublishing.EventPublisher

fun pokerGameNoEventAssert(test: TestPokerGame.() -> Unit) = TestPokerGame().test()

fun pokerGameTest(test: TestPokerGame.() -> Unit) = TestPokerGame()
        .apply(test)
        .assertExpectedEventsToMatchActualEvents()

fun pokerTableTest(test: PokerTable.() -> Unit) = PokerTable()
        .apply(test)
        .assertExpectedEventsToMatchActualEvents()

fun pokerTableTestNoEventAssert(test: PokerTable.() -> Unit) = PokerTable().test()

open class TestPokerGame(private val deck: PredeterminedCardDeck = PredeterminedCardDeck(listOf()),
                         private val eventStore: DummyEventStore = DummyEventStore(),
                         private val eventPublisher: EventPublisher = DummyEventPublisher(),
                         private val gameUseCases: GameService = GameUseCases(eventStore, eventPublisher, DummyActiveGames(), { deck }),
                         private val tableUseCases: TableService = TableUseCases(eventStore, gameUseCases, eventPublisher) { "gameId" }) {
    private val tableId: TableId = "1"
    private var gameId: GameId = "gameId"
    private var players: List<PlayerId>? = null
    private val expectedEvents = ArrayList<Event>()

    val newEvents get() = eventStore.newEvents

    fun given(givenActions: TestPokerGame.() -> Unit): TestPokerGame {
        val noopPokerGame = TestPokerGame(gameUseCases = DummyGameUseCases(),
                tableUseCases = DummyTableUseCases())
        noopPokerGame.givenActions()
        this.eventStore.given = mutableMapOf("1" to noopPokerGame.expectedEvents)
        this.players = noopPokerGame.players

        return this
    }

    fun startGame(predefinedCards: List<Card>): TestPokerGame {
        deck.provideNewCards(predefinedCards)
        val playersList = players!!.toList()

        createGame(playersList)
        startGame(playersList)

        return this
    }

    private fun startGame(playersList: List<PlayerId>) {
        gameUseCases.startGame(tableId, gameId, playersList)
        expectedEvents += GameStarted(playersList, DeckMother().deckOfHearts())
    }

    private fun createGame(playersList: List<PlayerId>) {
        tableUseCases.createGame()
        expectedEvents += GameCreated(gameId, playersList)
    }

    fun withCards(predefinedCards: List<Card>): TestPokerGame {
        deck.provideNewCards(predefinedCards)

        return this
    }

    fun withPlayers(vararg players: PlayerId): TestPokerGame {
        this.players = players.asList()
        players.forEach {
            tableUseCases.join(it)
            expectedEvents += PlayerJoinedTable(it)
        }

        return this
    }

    fun preflop(vararg playersWithCards: Pair<PlayerId, Hand>, actions: GameAction.() -> Unit = {}): TestPokerGame {
        expectedEvents += HandsAreDealt(mapOf(*playersWithCards))

        handleActions(actions)

        return this
    }

    fun flop(card1: Card, card2: Card, card3: Card, actions: GameAction.() -> Unit = {}): TestPokerGame {
        expectedEvents += FlopIsTurned(card1, card2, card3)

        handleActions(actions)

        return this
    }

    fun turn(card: Card, actions: GameAction.() -> Unit): TestPokerGame {
        expectedEvents += TurnIsTurned(card)

        handleActions(actions)

        return this
    }

    fun river(card: Card, actions: GameAction.() -> Unit): TestPokerGame {
        expectedEvents += RiverIsTurned(card)

        handleActions(actions)

        return this
    }

    fun action(actions: GameAction.() -> Unit): TestPokerGame {
        handleActions(actions)

        return this
    }

    private fun handleActions(actions: GameAction.() -> Unit) {
        val gameAction = GameAction("1",gameId!!, gameUseCases)
        actions.invoke(gameAction)
        expectedEvents += gameAction.expectedEvents

        expectedEvents += RoundCompleted()
    }

    // todo expectPlayersHaving(playerId, evaluatedRuleResult)
    fun expectWinner(expectedWinner: PlayerId): TestPokerGame {
        expectedEvents += PlayerWonGame(expectedWinner)

        return this
    }

    fun assertExpectedEventsToMatchActualEvents() {
        println("======================== EVENTS ========================")
        expectedEvents.forEachIndexed { index, event ->
            val same = eventStore.newEvents.size > index && event == eventStore.newEvents[index]

            println("$index. $same => $event || ${eventStore.newEvents.getOrNull(index)}")
        }
        println("========================================================")
        expect(expectedEvents).hasSize(eventStore.newEvents.size)
        expect(expectedEvents).toBe(eventStore.newEvents)
    }
}