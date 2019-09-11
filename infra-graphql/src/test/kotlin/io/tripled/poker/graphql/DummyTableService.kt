package io.tripled.poker.graphql

import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.HiddenCards
import io.tripled.poker.api.response.Player
import io.tripled.poker.api.response.Table
import io.tripled.poker.domain.GameId
import io.tripled.poker.domain.PlayerId
import org.springframework.stereotype.Component

@Component
class DummyTableService : TableService {

    override fun startGame(): GameId {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val players = mutableListOf<Player>()

    override fun join(name: String) {
        players += Player(name)
    }

    override fun getTable(playerId: PlayerId): Table = Table(players, HiddenCards(0), HiddenCards(0), HiddenCards(0), null, "")

    fun clear() {
        players.clear()
    }
}