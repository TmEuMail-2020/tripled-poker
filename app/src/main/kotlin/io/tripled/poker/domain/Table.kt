package io.tripled.poker.domain

typealias PlayerId = String

class
Table(tableState: TableState) {

    private val players = tableState.players

    fun join(name: String) = listOf<Event>(PlayerJoinedTable(name))

    fun startRound(deck: Deck): List<Event> {
        return if (players.size > 1) {
            val playerCards = dealPlayerCards(deck)
            listOf(
                    startRound(),
                    playerCards,
                    dealFloppedCards(deck),
                    dealTurn(deck),
                    determineWinner(playerCards.hands)
            )
        } else listOf()
    }

    private fun dealTurn(deck: Deck) = TurnIsTurned(deck.dealCard())

    private fun startRound() = RoundStarted()

    private fun determineWinner(dealtCards: Map<PlayerId, Hand>): PlayerWonRound = PlayerWonRound(dealtCards
            .toList()
            .maxBy { it.second.card1.score + it.second.card2.score }!!
            .first)

    private fun dealPlayerCards(deck: Deck): CardsAreDealt = CardsAreDealt(players.associateWith { Hand(deck.dealCard(), deck.dealCard()) })

    private fun dealFloppedCards(deck: Deck): FlopIsTurned = FlopIsTurned(deck.dealCard(), deck.dealCard(), deck.dealCard())

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
