package io.tripled.poker.domain.table

import io.tripled.poker.domain.Event
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId

interface TableRepository {
    fun findTableById(tableId: TableId): Table
    fun saveTable(tableId: TableId, events: List<Event>)

    fun projectTable(playerName: PlayerId): io.tripled.poker.app.api.response.Table
}