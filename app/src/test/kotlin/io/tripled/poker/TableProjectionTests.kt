package io.tripled.poker

import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCaseToRenameLater
import io.tripled.poker.api.response.Player
import io.tripled.poker.domain.PlayerJoinedTable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TableProjectionTests {

    private val eventStore = TestEventStore()
    private val tableService = TableUseCaseToRenameLater(eventStore)

    @Test
    internal fun `a table with players`() {
        eventStore.save(1, listOf(
                PlayerJoinedTable("Joe"),
                PlayerJoinedTable("Jef"))
        )

        val table = tableService.getTable()

        assertEquals(listOf(
                Player("Joe"),
                Player("Jef")
        ), table.players)
    }

}