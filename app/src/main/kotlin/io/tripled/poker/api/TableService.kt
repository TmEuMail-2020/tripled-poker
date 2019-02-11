package io.tripled.poker.api

import io.tripled.poker.api.response.Player
import io.tripled.poker.domain.Table

class TableService {
    private val table = Table()

    fun join(name: String) {
        table.join(name)
    }

    fun getTable(): io.tripled.poker.api.response.Table {
        val players = table.players.map { player -> Player(player.name) }.toList()
        return io.tripled.poker.api.response.Table(players);
    }

}
