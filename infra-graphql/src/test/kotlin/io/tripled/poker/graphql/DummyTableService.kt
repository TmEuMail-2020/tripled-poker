package io.tripled.poker.graphql

import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.Player
import io.tripled.poker.api.response.Table

class DummyTableService : TableService {
    override fun startRound() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val players = mutableListOf<Player>()

    override fun join(name: String) {
        players += Player(name)
    }

    override fun getTable(): Table = Table(players)
}