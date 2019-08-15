package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.hasSize
import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Table
import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore

fun pokerGameNoEventAssert(test: TestPokerGame.() -> Unit)
        = TestPokerGame().test()

fun pokerGameTest(test: TestPokerGame.() -> Unit)
        = TestPokerGame()
                .apply(test)
                .assertExpectedEventsToMatchActualEvents()

fun pokerTableTest(test: TestPokerGame.TestPokerTable.() -> Unit)
        = TestPokerGame.TestPokerTable()
                .apply(test)
                .assertExpectedEventsToMatchActualEvents()

fun pokerTableTestNoEventAssert(test: TestPokerGame.TestPokerTable.() -> Unit)
        = TestPokerGame.TestPokerTable()
        .apply(test)

open class TestPokerGame(private val deck: PredeterminedCardDeck = PredeterminedCardDeck(listOf()),
                         private val eventStore: DummyEventStore = DummyEventStore(),
                         private val gameUseCases: GameService = GameUseCases(eventStore,{deck}),
                         private val useCases: TableService = TableUseCases(eventStore, gameUseCases)) {

    private lateinit var players: List<PlayerId>
    private val expectedEvents = ArrayList<Event>()

    val newEvents get() = eventStore.newEvents

    fun given(givenActions: TestPokerGame.() -> Unit): TestPokerGame {
        val noopPokerGame = TestPokerGame(gameUseCases = DummyGameUseCases(),
                useCases = DummyTableUseCases())
        noopPokerGame.givenActions()
        this.eventStore.given = noopPokerGame.expectedEvents

        return this
    }

    fun withCards(predefinedCards: List<Card>): TestPokerGame {
        deck.provideNewCards(predefinedCards)

        return this
    }

    fun withPlayers(vararg players: PlayerId): TestPokerGame {
        this.players = players.asList()
        players.forEach {
            useCases.join(it)
            expectedEvents += PlayerJoinedTable(it)
        }

        return this
    }

    fun start(){

    }

    fun preflop(vararg playersWithCards: Pair<PlayerId, Hand>, actions: GameAction.() -> Unit): TestPokerGame {
        useCases.startGame()
        expectedEvents += GameStarted(players)
        expectedEvents += HandsAreDealt(DeckMother().deckOfHearts(), mapOf(*playersWithCards))

        handleActions(actions)

        return this
    }

    fun flop(card1: Card, card2: Card, card3: Card, actions: GameAction.() -> Unit): TestPokerGame {
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
        val gameAction = GameAction(gameUseCases)
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
            val same = event == eventStore.newEvents[index]

            println("$index. $same => $event || ${eventStore.newEvents[index]}")
        }
        println("========================================================")
        expect(expectedEvents).hasSize(eventStore.newEvents.size)
        expect(expectedEvents).toBe(eventStore.newEvents)
    }

    class GameAction(private val gameUseCases: GameService,
                     val expectedEvents: ArrayList<Event> = ArrayList()) {

        fun PlayerId.checks() = check(this)

        private fun check(playerId: PlayerId){
            gameUseCases.check(playerId)
            expectedEvents += PlayerChecked(playerId)
        }


        // TODO fold, raise, all-in, etc
    }

    class TestPokerTable (private val deck: PredeterminedCardDeck = PredeterminedCardDeck(listOf()),
                          private val eventStore: DummyEventStore = DummyEventStore(),
                          private val gameUseCases: GameService = GameUseCases(eventStore,{deck}),
                          private val tableUseCases: TableService = TableUseCases(eventStore, gameUseCases))
        : TestPokerGame(deck, eventStore, gameUseCases, TableUseCases(eventStore, gameUseCases)) {

        fun table(asPlayer: PlayerId, table: Table.() -> Unit): TestPokerTable {
            table.invoke(tableUseCases.getTable(asPlayer))

            return this
        }
    }

    class DummyEventStore(private val _newEvents: MutableList<Event> = mutableListOf()) : EventStore {
        var given: List<Event> = listOf()

        fun given(pokerBuilder: EventBuilder.() -> Unit) {
            val builder = EventBuilder()
            builder.pokerBuilder()
            given = builder.events
        }

        override fun save(id: Any, events: List<Event>) {
            _newEvents += events
        }

        override fun findById(id: Any): List<Event> {
            return given + _newEvents
        }

        val newEvents get() = _newEvents.toList()

        fun contains(element: Event) = this._newEvents.contains(element)

        class EventBuilder {
            val events = mutableListOf<Event>()

             fun playersJoin(vararg players: PlayerId) {
                events += players.map { PlayerJoinedTable(it) }
            }

            fun startGame(cardsInDeck: List<Card>, vararg playerHands: Pair<PlayerId, Hand>) {
                val playerIds = playerHands.map { p -> p.first }.toTypedArray()
                playersJoin(*playerIds)
                events += GameStarted(playerIds.toList())
                events += HandsAreDealt(cardsInDeck, mapOf(*playerHands))
            }
        }
    }

    class DummyGameUseCases : GameService {
        override fun check(player: String) = Unit
        override fun startGame(players: List<PlayerId>) = Unit
    }

    class DummyTableUseCases : TableService {
        override fun join(name: String) = Unit
        override fun startGame() = Unit
        override fun getTable(playerId: PlayerId): Table = null!!
    }
}