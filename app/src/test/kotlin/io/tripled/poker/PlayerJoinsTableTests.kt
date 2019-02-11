package io.tripled.poker

import io.tripled.poker.domain.Table
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlayerJoinsTableTests {

    @Test
    internal fun `a player can join the table`() {
        val table = Table()
        val name = "Joe"

        table.join(name)

        assertEquals(table.players.size, 1)
        assertEquals(table.players.first().name, name)
    }

    @Test
    internal fun `a new table has no players`() {
        val table = Table()


        assertEquals(table.players.size, 0)
    }




}