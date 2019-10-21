package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.message
import ch.tutteli.atrium.api.cc.en_GB.startsWith
import ch.tutteli.atrium.api.cc.en_GB.toThrow
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.of
import io.tripled.poker.dsl.pokerGameTest
import io.tripled.poker.dsl.pokerTableTestNoEventAssert
import io.tripled.poker.vocabulary.Suit.HEARTS
import io.tripled.poker.vocabulary.Value.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class HappyPokerTest {
    private val Joe = "Joe"
    private val Jef = "Jef"
    private val Kris = "Kris"

    @Test
    internal fun `test all usecases and events to play game with two players`() = pokerGameTest {
        withPlayers(Joe, Jef)
        startGame(DeckMother().deckOfHearts())
        preflop(
                Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
        ) {
            Joe.checks()
            Jef.checks()
        }
        flop(NINE of HEARTS,
                EIGHT of HEARTS,
                SEVEN of HEARTS
        ) {
            Joe.checks()
            Jef.checks()
        }
        turn(SIX of HEARTS) {
            Joe.checks()
            Jef.checks()
        }
        river(FIVE of HEARTS) {
            Joe.checks()
            Jef.checks()
        }
        expectWinner(Jef)
    }

    @Test
    internal fun `test all usecases and events to play game with two players 2`() = pokerGameTest {
        withPlayers(Joe, Jef)
        startGame(DeckMother().deckOfHearts())
        preflop(
                Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
        ) {
            Joe.checks()
            Jef.checks()
        }
        flop(NINE of HEARTS,
                EIGHT of HEARTS,
                SEVEN of HEARTS
        ) {
            Joe.checks()
            Jef.checks()
        }
        turn(SIX of HEARTS) {
            Joe.checks()
            Jef.checks()
        }
        river(FIVE of HEARTS) {
            Joe.checks()
            Jef.folds()
        }
        expectWinner(Joe)
    }
}
