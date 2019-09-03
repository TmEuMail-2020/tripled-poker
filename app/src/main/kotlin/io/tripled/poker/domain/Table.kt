package io.tripled.poker.domain

typealias PlayerId = String
typealias TableId = String

data class PlayerJoinedTable(val name: String) : Event
data class GameStarted(val gameId: GameId, val players: List<PlayerId>, val cardsInDeck: List<Card>) : Event

class Table(tableState: TableState) {
    private val players = tableState.players

    fun join(name: PlayerId) = if (valid(name)) listOf<Event>(PlayerJoinedTable(name)) else listOf()

    private fun valid(name: PlayerId) = name.isNotBlank() && !players.contains(name)

    fun startGame(gameId: GameId, deck: Deck) = sequence {
        if (players.size > 1)
            yield(GameStarted(gameId, players, deck.cards))
    }.toList()
}

data class TableState(val players: List<PlayerId>) {
    companion object {
        fun of(events: List<Event>) = TableState(players(events))

        private fun players(events: List<Event>): List<String> = events
                .filterEvents<PlayerJoinedTable>()
                .map { event -> event.name }
    }
}

