package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.of
import io.tripled.poker.dsl.DummyEventStore
import io.tripled.poker.dsl.pokerGameTest
import io.tripled.poker.dsl.pokerTableTest
import io.tripled.poker.projection.DslProjection
import org.junit.jupiter.api.Test

class DslProjectionTest {
    private val Joe = "Joe"
    private val Jef = "Jef"

    @Test
    internal fun `test to see if we can generated the dsl out of the event stream`() = pokerTableTest {
        given {
            withCards(DeckMother().deckOfHearts())
            withPlayers(Joe, Jef)
            preflop(
                    Joe to ((Value.TEN of Suit.HEARTS) and (Value.ACE of Suit.HEARTS)),
                    Jef to ((Value.KING of Suit.HEARTS) and (Value.QUEEN of Suit.HEARTS))
            ) {
                Joe.checks()
                Jef.checks()
            }
        }

        val dsl = DslProjection().dsl(DummyEventStore(this.eventStore.given.toMutableList() /* todo fixme */))

        expect(dsl).toBe(
        """
            withCards(TEN of HEARTS,ACE of HEARTS,KING of HEARTS,QUEEN of HEARTS,NINE of HEARTS,EIGHT of HEARTS,SEVEN of HEARTS,SIX of HEARTS,FIVE of HEARTS)
        """.trimIndent())
    }
}