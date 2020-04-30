package io.tripled.poker.domain.game

import io.tripled.poker.domain.Event
import io.tripled.poker.domain.cards.*
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
        val lastPlayer: PlayerId?)