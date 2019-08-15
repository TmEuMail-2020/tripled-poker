package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.isEmpty
import ch.tutteli.atrium.api.cc.en_GB.message
import ch.tutteli.atrium.api.cc.en_GB.startsWith
import ch.tutteli.atrium.api.cc.en_GB.toThrow
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.response.Suit.HEARTS
import io.tripled.poker.api.response.Value.*
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.of
import org.junit.jupiter.api.Test

class StartGameUseCaseTests {
    private val Joe = "Joe"
    private val Jef = "Jef"

    @Test
    internal fun `can't keep playing the game when it's done`() = pokerTableTestNoEventAssert {
        given {
            withCards(DeckMother().deckOfHearts())
            withPlayers(Joe, Jef)
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
        }

        expect {
            // -> execute action on done game
            while (true){
                action {
                    Joe.checks()
                    Jef.checks()
                }
            }
        }.toThrow<RuntimeException>{
            message { startsWith("t'is gedaan, zet u derover") }
        }
    }

    @Test
    internal fun `cannot start game with one player`() = pokerGameTest {
        given {
            withPlayers(Joe)
        }

        start()

        expect(newEvents).isEmpty()
    }
}