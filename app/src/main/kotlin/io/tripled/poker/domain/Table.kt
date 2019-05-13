package io.tripled.poker.domain

import java.util.Arrays.asList

typealias PlayerId = String

class Table(tableState: TableState) {

    private val players = tableState.players
    private val winnerDeterminer = WinnerDeterminer()
    private val hands = tableState.hands
    private val deck = PredeterminedCardDeck(tableState.remainingCards)
    private val countCalls = tableState.countChecks;
    private val theCardsOnTheTable = tableState.cardsOnTable

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
        result.add(determineWinner())
    }

    private fun everybodyChecked() = countCalls == players.size - 1

    private fun startBettingRound() = players.map { PlayerChecked(it) }

    private fun determineWinner() =
            PlayerWonGame(winnerDeterminer.determineWinner(hands, this.theCardsOnTheTable))

    // WTF?
    private fun startGame2(deck: Deck) = GameStarted(deck.cards)

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
        val countChecks: Int) {

    companion object {
        fun of(events: List<Event>) = TableState(players(events),
                hands(events),
                deck(events),
                cardsOnTable(events),
                countChecks(events))

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
    }

}
