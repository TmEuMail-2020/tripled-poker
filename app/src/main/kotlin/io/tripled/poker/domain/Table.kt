package io.tripled.poker.domain

typealias PlayerId = String

class Table(tableState: TableState) {

    private val players = tableState.players

    fun join(name: String) = listOf<Any>(PlayerJoinedTable(name))

    fun startRound(deck: Deck): List<Any> {
        return if (players.size > 1) {
            val dealtCards = dealCards(deck)
            listOf(
                    RoundStarted(),
                    CardsAreDealt(dealtCards),
                    PlayerWonRound(determineWinner(dealtCards))
            )
        } else listOf()
    }

    private fun determineWinner(dealtCards: Map<PlayerId, Hand>) = dealtCards
            .toList()
            .maxBy { it.second.card1.score + it.second.card2.score }!!
            .first

    private fun dealCards(deck: Deck) = players.associateWith { Hand(deck.dealCard(), deck.dealCard()) }

}

data class TableState(
        val players: List<PlayerId>) {

    companion object {
        fun of(events: List<Any>) = TableState(players(events))

        private fun players(events: List<Any>): List<String> {
            return events
                    .filterEvents<PlayerJoinedTable>()
                    .map { event -> event.name }
        }
    }

}
