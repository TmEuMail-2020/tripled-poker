package io.tripled.poker.graphql

import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.Player
import io.tripled.poker.api.response.Table

class DummyTableService : TableService {
    override fun startGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val players = mutableListOf<Player>()

    override fun join(name: String) {
        players += Player(name)
    }

    override fun getTable(name: String): Table = Table(players)
    fun clear() {
        players.clear()
    }
}