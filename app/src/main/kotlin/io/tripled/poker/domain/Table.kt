package io.tripled.poker.domain

typealias PlayerId = String

class Table(tableState: TableState) {

    private val players = tableState.players
    private val playersThatCalled = tableState.playersThatCalled
    private val winnerDeterminer = WinnerDeterminer()

    fun join(name: String) = listOf<Event>(PlayerJoinedTable(name))

    fun startGame(deck: Deck): List<Event> {
        if (players.size <= 1) return listOf()

        val playerCards = dealPlayerCards(deck)
        val flop = dealFlop(deck)
        val turn = dealTurn(deck)
        val river = dealRiver(deck)
        return listOf<Event>() +
                startGameWithDeck(deck) +
                playerCards +
                startBettingRound() +
                flop +
                startBettingRound() +
                turn +
                startBettingRound() +
                river +
                startBettingRound() +
                determineWinner(playerCards, flop, turn, river)

    }

    private fun startBettingRound() = players.map { PlayerCalled(it) }

    private fun determineWinner(playerCards: CardsAreDealt, flop: FlopIsTurned, turn: TurnIsTurned, river: RiverIsTurned) =
            PlayerWonGame(winnerDeterminer.determineWinner(playerCards.hands, listOf(flop.card1, flop.card2, flop.card3, turn.card, river.card)))

    private fun startGameWithDeck(deck: Deck) = GameStarted(deck.remainingCards())

    private fun dealPlayerCards(deck: Deck): CardsAreDealt = CardsAreDealt(players.associateWith { Hand(deck.dealCard(), deck.dealCard()) })

    private fun dealFlop(deck: Deck): FlopIsTurned = FlopIsTurned(deck.dealCard(), deck.dealCard(), deck.dealCard())

    private fun dealTurn(deck: Deck) = TurnIsTurned(deck.dealCard())

    private fun dealRiver(deck: Deck) = RiverIsTurned(deck.dealCard())

    fun call(name: PlayerId): List<Event> {
        val events = listOf(PlayerCalled(name))
        if (players.size == playersThatCalled.size) {
//            dealFlop()
        }
        return events
    }
}

data class TableState(
        val players: List<PlayerId>, val playersThatCalled: List<PlayerId>) {
    companion object {
        fun of(events: List<Event>) = TableState(players(events), playersThatCalled(events))

        private fun players(events: List<Event>): List<String> = events
                .filterEvents<PlayerJoinedTable>()
                .map { event -> event.name }

        // Todo: Of course we will have to do this per betting round... we know...
        private fun playersThatCalled(events: List<Event>): List<String> = events
                .filterEvents<PlayerCalled>()
                .map { event -> event.name }
    }

}
