package io.tripled.poker.projection

import io.tripled.poker.api.response.Player
import io.tripled.poker.api.response.Table
import io.tripled.poker.domain.PlayerJoinedTable

class TableProjection(events: List<Any>) {

    val table: Table

    init {
        table = Table(players(events))
    }

    private fun players(events: List<Any>): List<Player> {
        return events
                .filter { it is PlayerJoinedTable }
                .map { event ->
                    Player((event as PlayerJoinedTable).name)
                }
    }

}
