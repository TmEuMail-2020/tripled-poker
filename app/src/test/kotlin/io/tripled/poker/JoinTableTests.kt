package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.isEmpty
import ch.tutteli.atrium.verbs.expect
import org.junit.jupiter.api.Test

class JoinTableTests {
    private val Joe = "Joe"
    private val Jef = "Jef"

    @Test
    internal fun `a player can join the table`() = pokerGameTest {
        withPlayers(Joe)
    }

    @Test
    internal fun `a player can't join the table with an empty name`() = pokerGameNoEventAssert {
        withPlayers("")
        expect(newEvents).isEmpty()
    }

    @Test
    internal fun `a player can't join twice with the same name`() = pokerGameNoEventAssert {
        given {
            withPlayers(Jef)
        }
        withPlayers(Jef)

        expect(newEvents).isEmpty()
    }
}

