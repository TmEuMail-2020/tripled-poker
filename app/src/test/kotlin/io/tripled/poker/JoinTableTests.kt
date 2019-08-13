package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.*
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.domain.PlayerJoinedTable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JoinTableTests {
    @Test
    internal fun `a player can join the table`() = pokerTest {
        val Joe = "Joe"

        withPlayers(Joe)
    }

    @Test
    internal fun `a player can't join the table with an empty name`() = pokerTestNoEventAssert {
        withPlayers("")

        expect(newEvents).isEmpty()
    }

    @Test
    internal fun `a player can't join twice with the same name`() = pokerTestNoEventAssert {
        val Jef = "Jef"

        given {
            withPlayers(Jef)
        }
        withPlayers(Jef)

        expect(newEvents).isEmpty()
    }
}

