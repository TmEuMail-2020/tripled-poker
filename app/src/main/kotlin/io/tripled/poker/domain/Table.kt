package io.tripled.poker.domain

typealias PlayerId = String
typealias TableId = String

data class PlayerJoinedTable(val name: String) : Event
data class GameCreated(val gameId: GameId, val players: List<PlayerId>) : Event

class Table(tableState: TableState) {
    private val players = tableState.players

    fun join(name: PlayerId) = if (valid(name)) listOf<Event>(PlayerJoinedTable(name)) else listOf()

    private fun valid(name: PlayerId) = name.isNotBlank() && !players.contains(name)

    fun createGame(gameId: GameId) = sequence {
        if (players.size > 1) {
            yield(GameCreated(gameId, players))
        } else {
            throw RuntimeException("Can't start a game with only 1 player")
        }
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

