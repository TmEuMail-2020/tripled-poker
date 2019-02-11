package io.tripled.poker.domain

data class PlayerJoinedTable(val name: String)

class Table {

    fun join(name: String): PlayerJoinedTable {
        return PlayerJoinedTable(name)
    }

}