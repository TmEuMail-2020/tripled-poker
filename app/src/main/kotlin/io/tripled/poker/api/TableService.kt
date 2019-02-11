package io.tripled.poker.api

import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table
import io.tripled.poker.api.response.Player
import io.tripled.poker.domain.PlayerJoinedTable
import io.tripled.poker.domain.Table
import io.tripled.poker.eventsourcing.EventStore

class TableService(private val eventStore: EventStore) {

    fun join(name: String) {
        val table = Table(eventStore.findById(1))
        eventStore.save(1, table.join(name))
    }

    fun getTable(): io.tripled.poker.api.response.Table {
        val table = Table(eventStore.findById(1))

        val players = table.players.map { player -> Player(player.name) }.toList()
        return io.tripled.poker.api.response.Table(players);
    }

}
