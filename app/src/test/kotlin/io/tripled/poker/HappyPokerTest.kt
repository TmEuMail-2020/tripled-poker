package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.*
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Suit.HEARTS
import io.tripled.poker.api.response.Value.*
import io.tripled.poker.domain.*
import org.junit.jupiter.api.Test

class HappyPokerTest {
    private val pokerGame = TestPokerGame()

    @Test
    internal fun `test all usecases and events to play game with two players`() {
        val Joe = "Joe"
        val Jef = "Jef"

        pokerGame
                .withCards(DeckMother().deckOfHearts())
                .withPlayers(Joe, Jef)
                .preflop(
                        Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                        Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
                ) {
                     check(Joe)
                     check(Jef)
                }
                .flop(NINE of HEARTS,
                      EIGHT of HEARTS,
                      SEVEN of HEARTS
                ) {
                    check(Joe)
                    check(Jef)
                }
                .turn(SIX of HEARTS) {
                    check(Joe)
                    check(Jef)
                }
                .river(FIVE of HEARTS) {
                    check(Joe)
                    check(Jef)
                }
                .expectWinner(Jef)
                .assertExpectedEventsToMatchActualEvents()
    }
}

class TestPokerGame(private val deck: PredeterminedCardDeck = PredeterminedCardDeck(listOf()),
                    private val eventStore: DummyEventStore = DummyEventStore(),
                    private val gameUseCases: GameService = GameUseCases(eventStore,{deck}),
                    private val useCases: TableService = TableUseCases(eventStore, gameUseCases)) {

    private lateinit var players: List<PlayerId>
    private val expectedEvents = ArrayList<Event>()

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

    fun preflop(vararg playersWithCards: Pair<PlayerId, Hand>, actions: GameAction.() -> Unit): TestPokerGame {
        useCases.startGame()
        expectedEvents.add(GameStarted(players))

        expectedEvents.add(HandsAreDealt(DeckMother().deckOfHearts(), mapOf(*playersWithCards)))

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

    private fun handleActions(actions: GameAction.() -> Unit) {
        val gameAction = GameAction(gameUseCases)
        actions.invoke(gameAction)
        expectedEvents.addAll(gameAction.expectedEvents)

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
        fun check(playerId: PlayerId){
            gameUseCases.check(playerId)
            expectedEvents.add(PlayerChecked(playerId))
        }

        // TODO fold, raise, all-in, etc
    }
}