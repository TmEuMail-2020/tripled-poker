package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.response.Suit.*
import io.tripled.poker.api.response.Value.*
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.of
import io.tripled.poker.dsl.DummyEventStore
import io.tripled.poker.dsl.pokerTableTest
import io.tripled.poker.projection.DslProjection
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
        }

        val dsl = DslProjection().dsl(DummyEventStore(this.eventStore.given))

        expect(dsl).toBe(
        """
            withPlayers(Joe, Jef)
            startGame(TEN of HEARTS,ACE of HEARTS,KING of HEARTS,QUEEN of HEARTS,NINE of HEARTS,EIGHT of HEARTS,SEVEN of HEARTS,SIX of HEARTS,FIVE of HEARTS)
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
        """.trimIndent())
    }
}