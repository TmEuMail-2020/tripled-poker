package io.tripled.poker.domain

import io.tripled.poker.vocabulary.PlayerId

data class GameStarted(val players: List<PlayerId>, val cardsInDeck: List<Card>) : Event
data class HandsAreDealt(val hands: Map<PlayerId, Hand>) : Event
data class PlayerChecked(val name: PlayerId) : Event
data class PlayerFolded(val name: PlayerId) : Event
data class PlayerWonGame(val name: PlayerId) : Event
data class FlopIsTurned(val card1: Card, val card2: Card, val card3: Card) : Event
data class TurnIsTurned(val card: Card) : Event
data class RiverIsTurned(val card: Card) : Event

class Game(gameState: GameState) {
    private val lastPlayer = gameState.lastPlayer
    private val deck = PredeterminedCardDeck(gameState.remainingCards)
    private var countChecks = gameState.countChecks
    private val gamePhase = gameState.gamePhase
    private val bestHandsCalculator = BestHandsCalculator()
    private val hands = gameState.hands
    private val players = gameState.players

    fun start(players: List<PlayerId>, deck: Deck) = listOf(
            GameStarted(players, deck.cards),
            dealPlayerHands(players, deck)
    )

    fun fold(currentPlayer: PlayerId) = safeGameAction(currentPlayer){
        yield(PlayerFolded(currentPlayer))

        if (players.size == 2) yield(PlayerWonGame(players.find { p -> p != currentPlayer }!!))
    }

    private fun safeGameAction(currentPlayer: PlayerId, gameAction: suspend SequenceScope<Event>.() -> Unit): List<Event> = sequence<Event> {
        ensurePlayerStillInGame(currentPlayer)
        ensurePlayersTurn(currentPlayer)
        gameAction()
    }.toList()

    private fun ensurePlayerStillInGame(player: PlayerId) {
        if (!players.contains(player)) {
            throw RuntimeException("gast, ge speelt nimeer mee")
        }
    }

    fun check(currentPlayer: PlayerId) = safeGameAction(currentPlayer){
        if (GamePhase.DONE == gamePhase) {
            throw RuntimeException("t'is gedaan, zet u derover")
        }

        countChecks++
        yield(PlayerChecked(currentPlayer))

        if (everybodyCheckedThisRound()) {
            when (gamePhase) {
                GamePhase.PRE_FLOP -> yieldAll(flop())
                GamePhase.FLOP -> yieldAll(turn())
                GamePhase.TURN -> yieldAll(river())
                GamePhase.RIVER -> yield(PlayerWonGame(bestHandsCalculator.calculateBest(hands, listOf())))
                GamePhase.DONE -> Unit
            }
        }
    }.toList()

    private fun ensurePlayersTurn(currentPlayer: PlayerId) {
        val playersTurn = players[(players.indexOf(lastPlayer) + 1) % players.size]
        if (currentPlayer != playersTurn) {
            throw RuntimeException("t'is nie oan aaa e")
        }
    }

    private fun dealPlayerHands(players: List<PlayerId>, deck: Deck): HandsAreDealt = HandsAreDealt(players.associateWith { Hand(deck.dealCard(), deck.dealCard()) })

    private fun flop(): List<Event> = listOf(dealFlop(deck))

    private fun turn(): List<Event> = listOf(dealTurn(deck))

    private fun river(): List<Event> = listOf(dealRiver(deck))

    private fun everybodyCheckedThisRound() = (countChecks % players.size) == 0

    private fun dealFlop(deck: Deck): FlopIsTurned = FlopIsTurned(deck.dealCard(), deck.dealCard(), deck.dealCard())

    private fun dealTurn(deck: Deck) = TurnIsTurned(deck.dealCard())

    private fun dealRiver(deck: Deck) = RiverIsTurned(deck.dealCard())

}

data class GameState(
        val players: List<PlayerId>,
        val hands: Map<PlayerId, Hand>,
        val remainingCards: List<Card>,
        val countChecks: Int,
        val gamePhase: GamePhase,
        val lastPlayer: PlayerId?) {

    companion object {
        fun of(events: List<Event>) = GameState(players(events),
                hands(events),
                remainingCards(events),
                countChecks(events),
                phase(events),
                lastPlayer(events))

        private fun countChecks(events: List<Event>): Int =
                events.filterEvents<PlayerChecked>().size

        private fun lastPlayer(events: List<Event>): PlayerId? {
            return events.filter { e -> e is PlayerFolded || e is PlayerChecked }.lastOrNull { e ->
                return when (e) {
                    is PlayerFolded -> e.name
                    is PlayerChecked -> e.name
                    else -> ""
                }
            }?.toString() ?: players(events).lastOrNull()

        }

        private fun players(events: List<Event>): List<PlayerId> {
            val allPlayers = hands(events).keys.toList()
            val foldedPlayers = events.filterEvents<PlayerFolded>().map { t -> t.name }

            return allPlayers - foldedPlayers
        }

        private fun hands(events: List<Event>): Map<PlayerId, Hand> {
            val lastEventOrNull = events
                    .lastEventOrNull<HandsAreDealt>()
            return lastEventOrNull?.hands ?: mapOf()
        }

        private fun remainingCards(events: List<Event>): List<Card> {
            val lastEventOrNull = events
                    .lastEventOrNull<GameStarted>() ?: return listOf()

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