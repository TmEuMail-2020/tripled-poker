package io.tripled.poker.domain

data class HandsAreDealt(val cardsInDeck: List<Card>, val hands: Map<PlayerId, Hand>) : Event
data class RoundCompleted(val Noop: String = "Guido") : Event
data class PlayerChecked(val name: PlayerId) : Event
data class PlayerWonGame(val name: PlayerId) : Event
data class FlopIsTurned(val card1: Card, val card2: Card, val card3: Card) : Event
data class TurnIsTurned(val card: Card) : Event
data class RiverIsTurned(val card: Card) : Event

typealias GameId = String

class Game(gameState: GameState) {
    private val deck = PredeterminedCardDeck(gameState.remainingCards)
    private var countChecks = gameState.countChecks
    private val gamePhase = gameState.gamePhase
    private val winnerDeterminer = WinnerDeterminer()
    private val hands = gameState.hands
    private val players = gameState.players

    fun start(players: List<PlayerId>, deck: Deck) = sequence { yield(dealPlayerHands(players, deck)) }.toList()

    fun check(player: PlayerId) = sequence {
        if (GamePhase.DONE == gamePhase) {
            throw RuntimeException("t'is gedaan, zet u derover")
        }

        countChecks++
        yield(PlayerChecked(player))

        if (everybodyCheckedThisRound()) {
            yield(RoundCompleted())

            when (gamePhase) {
                GamePhase.PRE_FLOP -> yieldAll(flop())
                GamePhase.FLOP -> yieldAll(turn())
                GamePhase.TURN -> yieldAll(river())
                GamePhase.RIVER -> yieldAll(determineWinner())
            }
        }
    }.toList()

    private fun dealPlayerHands(players: List<PlayerId>, deck: Deck): HandsAreDealt = HandsAreDealt(deck.cards, players.associateWith { Hand(deck.dealCard(), deck.dealCard()) })

    private fun flop(): List<Event> = listOf(dealFlop(deck))

    private fun turn(): List<Event> = listOf(dealTurn(deck))

    private fun river(): List<Event> = listOf(dealRiver(deck))

    private fun determineWinner(): List<Event> = listOf(determineWinnerEvent())

    private fun everybodyCheckedThisRound() = (countChecks % players.size) == 0

    private fun determineWinnerEvent() =
            PlayerWonGame(winnerDeterminer.determineWinner(hands, listOf()))

    private fun dealFlop(deck: Deck): FlopIsTurned = FlopIsTurned(deck.dealCard(), deck.dealCard(), deck.dealCard())

    private fun dealTurn(deck: Deck) = TurnIsTurned(deck.dealCard())

    private fun dealRiver(deck: Deck) = RiverIsTurned(deck.dealCard())

}

data class GameState(
        val players: List<PlayerId>,
        val hands: Map<PlayerId, Hand>,
        val remainingCards: List<Card>,
        val countChecks: Int,
        val gamePhase: GamePhase) {

    companion object {
        fun of(events: List<Event>) = GameState(players(events),
                hands(events),
                deck(events),
                countChecks(events),
                phase(events))

        private fun countChecks(events: List<Event>): Int =
                events.filterEvents<PlayerChecked>().size

        private fun players(events: List<Event>): List<PlayerId> = hands(events).keys.toList()

        private fun hands(events: List<Event>): Map<PlayerId, Hand> {
            val lastEventOrNull = events
                    .lastEventOrNull<HandsAreDealt>()
            return lastEventOrNull?.hands ?: mapOf()
        }

        private fun deck(events: List<Event>): List<Card> {
            val lastEventOrNull = events
                    .lastEventOrNull<HandsAreDealt>() ?: return listOf()

            val cards = lastEventOrNull.cardsInDeck.toMutableList()
            return events.fold(cards) { _, event ->
                when (event) {
                    is HandsAreDealt -> cards.removeAll(
                            event.hands.flatMap { it.value.cards() }
                    )
                    is FlopIsTurned -> cards.removeAll(
                            listOf(event.card1, event.card2, event.card3)
                    )
                    is TurnIsTurned -> cards.remove(event.card)
                    is RiverIsTurned -> cards.remove(event.card)
                }
                cards
            }
        }

        private fun phase(events: List<Event>): GamePhase =
                events.fold(GamePhase.PRE_FLOP, { acc, event ->
                    when (event) {
                        is FlopIsTurned -> GamePhase.FLOP
                        is TurnIsTurned -> GamePhase.TURN
                        is RiverIsTurned -> GamePhase.RIVER
                        is PlayerWonGame -> GamePhase.DONE
                        else -> acc
                    }
                })
    }
}