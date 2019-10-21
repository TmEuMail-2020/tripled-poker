package io.tripled.poker.gyt

import io.tripled.poker.gyt.dsl.pokerGameScenario
import org.junit.jupiter.api.Test

class NewDSLPokerTest {
    private val Joe = "Joe"
    private val Jef = "Jef"

    @Test
    internal fun `1 player join`() = pokerGameScenario {
        given {
        }
        action {
            playerJoins(Joe)
        }
        verify {
            playerJoined(Joe)
        }
    }

    @Test
    internal fun `2 players join`() = pokerGameScenario {
        given {
            playerIsPresent(Jef)
            playerIsPresent(Joe)
        }
        action {
            startGame(listOf(Jef, Joe))
        }
        verify {
            gameHasBeenCreated(listOf(Jef, Joe))
            gameHasStarted(listOf(Jef, Joe))
            handsAreDealt()
        }
    }

    @Test
    internal fun `player can check`() = pokerGameScenario {
        given {
            playerIsPresent(Jef)
            playerIsPresent(Joe)
            gameHasStarted(listOf(Jef, Joe))
        }
        action {
            playerChecks(Joe)
        }
        verify {
            playerHasChecked(Joe)
        }
    }

    @Test
    internal fun `last player can check`() = pokerGameScenario {
        given {
            playerIsPresent(Jef)
            playerIsPresent(Joe)
            gameHasStarted(listOf(Jef, Joe))
            playerHasChecked(Jef)
        }
        action {
            playerChecks(Joe)
        }
        verify {
            playerHasChecked(Joe)
        }
    }
}