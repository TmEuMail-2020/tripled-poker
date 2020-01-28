package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.dsl.pokerGameTest2
import org.junit.jupiter.api.Test

class HappyPokerTest2 {
    private val Joe = "Joe"
    private val Jef = "Jef"

    @Test
    internal fun `test all usecases and events to play game with two players`() = pokerGameTest2 {
        given {
            table {
                + Joe
                + Jef
            }
        }

        then {
            pg -> expect(pg.tableState.players).toBe(listOf(Joe, Jef))
        }
    }
}
