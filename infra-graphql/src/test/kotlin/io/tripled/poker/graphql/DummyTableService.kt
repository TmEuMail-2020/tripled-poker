package io.tripled.poker.graphql

import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.HiddenCards
import io.tripled.poker.api.response.Player
import io.tripled.poker.api.response.Table
import org.springframework.stereotype.Component

@Component
class DummyTableService(private val assumeUser: AssumeUser) : TableService {

    override fun createGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val players = mutableListOf<Player>()

    override fun join() {
        players += Player(assumeUser.assumedPlayerId)
    }

    override fun getTable(): Table = Table(players, HiddenCards(0), HiddenCards(0), HiddenCards(0), null, "")

    fun clear() {
        players.clear()
    }
}