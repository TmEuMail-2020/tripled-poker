package io.tripled.poker.domain

class Player(val name: String)

data class PlayerJoinedTable(val name: String)

class Table(private val events: List<Any>) {
    val players = mutableListOf<Player>()

    init {
        events.forEach { event ->
            when (event) {
                is PlayerJoinedTable -> players.add(Player(event.name))
            }
        }
    }

    fun join(name: String): PlayerJoinedTable {
        return PlayerJoinedTable(name)
    }


}