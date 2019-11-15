package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.of
import io.tripled.poker.dsl.DummyEventStore
import io.tripled.poker.dsl.pokerTableTest
import io.tripled.poker.projection.DslProjection
import io.tripled.poker.vocabulary.Suit.HEARTS
import io.tripled.poker.vocabulary.Value.*
import org.junit.jupiter.api.Test

class DslProjectionTest {

    private val Joe = "Joe"
    private val Jef = "Jef"

    @Test
    internal fun `test to see if we can generated the dsl out of the event stream`() = pokerTableTest {
        given {
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

        val dsl = DslProjection(DummyEventStore(this.eventStore.given)).dsl()

        expect(dsl).toBe(
        """
            withPlayers(Joe, Jef)
            startGame(listOf(TEN of HEARTS,ACE of HEARTS,KING of HEARTS,QUEEN of HEARTS,NINE of HEARTS,EIGHT of HEARTS,SEVEN of HEARTS,SIX of HEARTS,FIVE of HEARTS,FOUR of HEARTS,THREE of HEARTS,TWO of HEARTS))
            preflop(
                Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
            ) {

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
        """.trimIndent())
    }


    @Test
    internal fun `test to see it works with an incomplete event stream`() = pokerTableTest {
        given {
            withPlayers(Joe, Jef)
            startGame(DeckMother().deckOfHearts())
            preflop(
                    Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                    Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
            ) {
                Joe.checks()
                Jef.checks()
            }
        }

        val dsl = DslProjection(DummyEventStore(this.eventStore.given)).dsl()

        expect(dsl).toBe(
                """
            withPlayers(Joe, Jef)
            startGame(listOf(TEN of HEARTS,ACE of HEARTS,KING of HEARTS,QUEEN of HEARTS,NINE of HEARTS,EIGHT of HEARTS,SEVEN of HEARTS,SIX of HEARTS,FIVE of HEARTS,FOUR of HEARTS,THREE of HEARTS,TWO of HEARTS))
            preflop(
                Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
            ) {

            }
        """.trimIndent())
    }
}