package io.tripled.poker

import io.tripled.poker.api.response.Suit.HEARTS
import io.tripled.poker.api.response.Value.*
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.of
import org.junit.jupiter.api.Test

class HappyPokerTest {
    private val Joe = "Joe"
    private val Jef = "Jef"

    @Test
    internal fun `test all usecases and events to play game with two players`() = pokerGameTest {
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
        expectWinner(Jef)
    }
}