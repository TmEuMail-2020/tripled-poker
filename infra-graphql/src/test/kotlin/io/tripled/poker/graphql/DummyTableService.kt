package io.tripled.poker.graphql

import io.tripled.poker.app.api.TableService
import io.tripled.poker.app.api.response.HiddenCards
import io.tripled.poker.app.api.response.Player
import io.tripled.poker.app.api.response.Table
import io.tripled.poker.domain.Users
import org.springframework.stereotype.Component

@Component
class DummyTableService(private val users: Users) : TableService {

    override fun createGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val players = mutableListOf<Player>()

    override fun join() {
        players += Player(users.currentUser.playerId)
    }

    override fun getTable(): Table = Table(users.currentUser.playerId , players, HiddenCards(0), HiddenCards(0), HiddenCards(0), null, "")

    fun clear() {
        players.clear()
    }
}