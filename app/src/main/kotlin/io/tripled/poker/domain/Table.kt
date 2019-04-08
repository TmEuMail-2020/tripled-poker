package io.tripled.poker.domain

typealias PlayerId = String

class Table(tableState: TableState) {

    private val players = tableState.players
    private val winnerDeterminer = WinnerDeterminer()

    fun join(name: String) = listOf<Event>(PlayerJoinedTable(name))

    fun startGame(deck: Deck): List<Event> {
        if (players.size <= 1) return listOf()

        val playerCards = dealPlayerCards(deck)
        val flop = dealFlop(deck)
        val turn = dealTurn(deck)
        val river = dealRiver(deck)
        val result = mutableListOf(
                startGame(),
                playerCards)
        result.addAll(startBettingRound())
        result.add(flop)
        result.addAll(startBettingRound())
        result.add(turn)
        result.addAll(startBettingRound())
        result.add(river)
        result.addAll(startBettingRound())
        result.add(determineWinner(playerCards, flop, turn, river))
        return result

    }

    private fun startBettingRound() = players.map { PlayerCalled(it) }

    private fun determineWinner(playerCards: CardsAreDealt, flop: FlopIsTurned, turn: TurnIsTurned, river: RiverIsTurned) =
            PlayerWonGame(winnerDeterminer.determineWinner(playerCards.hands, listOf(flop.card1, flop.card2, flop.card3, turn.card, river.card)))

    private fun startGame() = GameStarted()

    private fun dealPlayerCards(deck: Deck): CardsAreDealt = CardsAreDealt(players.associateWith { Hand(deck.dealCard(), deck.dealCard()) })

    private fun dealFlop(deck: Deck): FlopIsTurned = FlopIsTurned(deck.dealCard(), deck.dealCard(), deck.dealCard())

    private fun dealTurn(deck: Deck) = TurnIsTurned(deck.dealCard())

    private fun dealRiver(deck: Deck) = RiverIsTurned(deck.dealCard())
}

data class TableState(
        val players: List<PlayerId>) {

    companion object {
        fun of(events: List<Event>) = TableState(players(events))

        private fun players(events: List<Event>): List<String> = events
                .filterEvents<PlayerJoinedTable>()
                .map { event -> event.name }
    }

}
