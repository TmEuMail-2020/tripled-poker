package io.tripled.poker

import io.tripled.poker.api.response.Suit.HEARTS
import io.tripled.poker.api.response.Value.*
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.of
import org.junit.jupiter.api.Test

class HappyPokerTest {
    private val pokerGame = TestPokerGame()

    @Test
    internal fun `test all usecases and events to play game with two players`() {
        val Joe = "Joe"
        val Jef = "Jef"

        pokerGame
                .withCards(DeckMother().deckOfHearts())
                .withPlayers(Joe, Jef)
                .preflop(
                        Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                        Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
                ) {
                     check(Joe)
                     check(Jef)
                }
                .flop(NINE of HEARTS,
                      EIGHT of HEARTS,
                      SEVEN of HEARTS
                ) {
                    check(Joe)
                    check(Jef)
                }
                .turn(SIX of HEARTS) {
                    check(Joe)
                    check(Jef)
                }
                .river(FIVE of HEARTS) {
                    check(Joe)
                    check(Jef)
                }
                .expectWinner(Jef)
                .assertExpectedEventsToMatchActualEvents()
    }
}