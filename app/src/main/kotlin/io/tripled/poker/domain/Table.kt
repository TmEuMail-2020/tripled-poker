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
                    RoundStarted(),
                    playerCards,
                    dealFloppedCards(deck),
                    PlayerWonRound(determineWinner(playerCards.hands))
            )
        } else listOf()
    }

    private fun determineWinner(dealtCards: Map<PlayerId, Hand>) = dealtCards
            .toList()
            .maxBy { it.second.card1.score + it.second.card2.score }!!
            .first

    private fun dealPlayerCards(deck: Deck): CardsAreDealt {
        return CardsAreDealt(players.associateWith { Hand(deck.dealCard(), deck.dealCard()) })
    }

    private fun dealFloppedCards(deck: Deck): FlopIsTurned {
        return FlopIsTurned(deck.dealCard(), deck.dealCard(), deck.dealCard())
    }

}

data class TableState(
        val players: List<PlayerId>) {

    companion object {
        fun of(events: List<Event>) = TableState(players(events))

        private fun players(events: List<Event>): List<String> {
            return events
                    .filterEvents<PlayerJoinedTable>()
                    .map { event -> event.name }
        }
    }

}
