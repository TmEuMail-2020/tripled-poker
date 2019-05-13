package io.tripled.poker.domain

import sun.audio.AudioPlayer.player
import java.util.Arrays.asList

typealias PlayerId = String

class Table(tableState: TableState) {

    private val players = tableState.players
    private val winnerDeterminer = WinnerDeterminer()
    private val hands = tableState.hands
    private val deck = PredeterminedCardDeck(tableState.cards)
    private val countCalls = tableState.countChecks;

    fun join(name: String) = listOf<Event>(PlayerJoinedTable(name))

    fun startGame(deck: Deck): List<Event> {
        if (players.size <= 1) return listOf()

        return mutableListOf(
                startGame2(deck),
                dealPlayerHands(deck))
    }

    fun check(player: PlayerId): List<Event> {
        val result = mutableListOf<Event>()
        result.add(PlayerChecked(player))

        if (everybodyChecked()) {
            result.add(RoundCompleted())
            result.addAll(flop())
        }
        return result
    }

    fun flop(): List<Event> {
        val result = mutableListOf<Event>()
        doEverythingElse(result)
        return result
    }

    private fun doEverythingElse(result: MutableList<Event>) {
        val flop = dealFlop(deck)
        result.add(flop)

        val turn = dealTurn(deck)
        val river = dealRiver(deck)
        result.addAll(startBettingRound())
        result.add(turn)
        result.addAll(startBettingRound())
        result.add(river)
        result.addAll(startBettingRound())
        result.add(determineWinner(flop, turn, river))
    }

    private fun everybodyChecked() = countCalls == players.size - 1

    private fun startBettingRound() = players.map { PlayerChecked(it) }

    private fun determineWinner(flop: FlopIsTurned, turn: TurnIsTurned, river: RiverIsTurned) =
            PlayerWonGame(winnerDeterminer.determineWinner(hands, listOf(flop.card1, flop.card2, flop.card3, turn.card, river.card)))

    private fun startGame2(deck: Deck) = GameStarted(deck.cards)

    private fun dealPlayerHands(deck: Deck): HandsAreDealt = HandsAreDealt(players.associateWith { Hand(deck.dealCard(), deck.dealCard()) })

    private fun dealFlop(deck: Deck): FlopIsTurned = FlopIsTurned(deck.dealCard(), deck.dealCard(), deck.dealCard())

    private fun dealTurn(deck: Deck) = TurnIsTurned(deck.dealCard())

    private fun dealRiver(deck: Deck) = RiverIsTurned(deck.dealCard())
}

data class TableState(
        val players: List<PlayerId>,
        val hands: Map<PlayerId, Hand>,
        val cards: List<Card>,
        val countChecks: Int) {

    companion object {
        fun of(events: List<Event>) = TableState(players(events), hands(events), deck(events), countChecks(events))

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
    }

}
