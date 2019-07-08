package io.tripled.poker.domain

import java.util.Arrays.asList

typealias PlayerId = String

class Table(tableState: TableState) {

    private val players = tableState.players
    private val winnerDeterminer = WinnerDeterminer()
    private val hands = tableState.hands
    private val deck = PredeterminedCardDeck(tableState.remainingCards)
    private val countCalls = tableState.countChecks
    private val theCardsOnTheTable = tableState.cardsOnTable
    private val gamePhase = tableState.gamePhase

    fun join(name: PlayerId) = if (valid(name)) listOf<Event>(PlayerJoinedTable(name)) else listOf()

    private fun valid(name: PlayerId) = name.isNotBlank() && !players.contains(name)

    fun startGame(deck: Deck) = sequence {
        if (players.size > 1)
            yieldAll(listOf(
                    GameStarted(deck.cards),
                    dealPlayerHands(deck)
            ))
    }.toList()

    fun check(player: PlayerId) = sequence {
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

    fun flop(): List<Event> = listOf(dealFlop(deck))

    fun turn(): List<Event> = listOf(dealTurn(deck))

    fun river(): List<Event> = listOf(dealRiver(deck))

    fun determineWinner(): List<Event> = listOf(determineWinnerEvent())

    private fun everybodyCheckedThisRound() = ((countCalls + 1) % players.size) == 0

    private fun determineWinnerEvent() =
            PlayerWonGame(winnerDeterminer.determineWinner(hands, this.theCardsOnTheTable))

    private fun dealPlayerHands(deck: Deck): HandsAreDealt = HandsAreDealt(players.associateWith { Hand(deck.dealCard(), deck.dealCard()) })

    private fun dealFlop(deck: Deck): FlopIsTurned = FlopIsTurned(deck.dealCard(), deck.dealCard(), deck.dealCard())

    private fun dealTurn(deck: Deck) = TurnIsTurned(deck.dealCard())

    private fun dealRiver(deck: Deck) = RiverIsTurned(deck.dealCard())
}

data class TableState(
        val players: List<PlayerId>,
        val hands: Map<PlayerId, Hand>,
        val remainingCards: List<Card>,
        val cardsOnTable: List<Card>,
        val countChecks: Int,
        val gamePhase: GamePhase) {

    companion object {
        fun of(events: List<Event>) = TableState(players(events),
                hands(events),
                deck(events),
                cardsOnTable(events),
                countChecks(events), phase(events))

        private fun cardsOnTable(events: List<Event>): List<Card> {
            val cardsOnTable = listOf<Card>()

            return events.fold(cardsOnTable) { acc, event ->
                when (event) {
                    is FlopIsTurned -> acc +
                            asList(event.card1, event.card2, event.card3)

                    is TurnIsTurned -> acc + event.card
                    is RiverIsTurned -> acc + event.card
                }
                cardsOnTable
            }
        }

        private fun countChecks(events: List<Event>): Int =
                events.filterEvents<PlayerChecked>().size


        private fun players(events: List<Event>): List<String> = events
                .filterEvents<PlayerJoinedTable>()
                .map { event -> event.name }


        private fun hands(events: List<Event>): Map<PlayerId, Hand> {
            val lastEventOrNull = events
                    .lastEventOrNull<HandsAreDealt>()
            return lastEventOrNull?.hands ?: mapOf()
        }

        private fun deck(events: List<Event>): List<Card> {
            val lastEventOrNull = events
                    .lastEventOrNull<GameStarted>()
            if (lastEventOrNull == null)
                return listOf()

            val cards = lastEventOrNull.cards.toMutableList()
            return events.fold(cards) { _, event ->
                when (event) {
                    is HandsAreDealt -> cards.removeAll(
                            event.hands.flatMap { it.value.cards() }
                    )
                    is FlopIsTurned -> cards.removeAll(
                            asList(event.card1, event.card2, event.card3)
                    )
                    is TurnIsTurned -> cards.remove(event.card)
                    is RiverIsTurned -> cards.remove(event.card)
                }
                cards
            }
        }

        private fun phase(events: List<Event>): GamePhase =
                events.fold(GamePhase.PRE_FLOP, { acc, event ->
                    when(event) {
                        is FlopIsTurned -> GamePhase.FLOP
                        is TurnIsTurned -> GamePhase.TURN
                        is RiverIsTurned -> GamePhase.RIVER
                        else -> acc
                    }
                })
    }

}
