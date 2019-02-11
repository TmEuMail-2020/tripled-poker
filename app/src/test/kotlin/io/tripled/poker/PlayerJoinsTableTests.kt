package io.tripled.poker

import io.tripled.poker.api.TableService
import io.tripled.poker.domain.Table
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlayerJoinsTableTests {

    private val tableService = TableService()

    @Test
    internal fun `a player can join the table`() {
        val name = "Joe"

        tableService.join(name)
        val table = tableService.getTable()

        assertEquals(table.players.size, 1)
        assertEquals(table.players.first().name, name)
    }

    @Test
    internal fun `a new table has no players`() {
        val table = Table()

        assertEquals(table.players.size, 0)
    }




}