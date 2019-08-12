package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.*
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.domain.PlayerJoinedTable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JoinTableTests {

    private var pokerGame = TestPokerGame()

    @BeforeEach
    internal fun setUp() {
        pokerGame = TestPokerGame()
    }

    @Test
    internal fun `a player can join the table`() {
        pokerGame
                .withPlayers("Joe")

        expect(pokerGame.newEvents).contains.inOrder.only.values(PlayerJoinedTable("Joe"))
    }

    @Test
    internal fun `a player can't join the table with an empty name`() {
        pokerGame
                .withPlayers("")

        expect(pokerGame.newEvents).isEmpty()
    }

    @Test
    internal fun `a player can't join twice with the same name`() {
        pokerGame
                .given {
                    withPlayers("Jef")
                }
                .withPlayers("Jef")

        expect(pokerGame.newEvents).isEmpty()
    }

}

