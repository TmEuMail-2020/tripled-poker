package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.domain.DeckMother
import io.tripled.poker.dsl.DummyEventStore
import io.tripled.poker.dsl.pokerTableTest
import io.tripled.poker.projection.DslProjection
import org.junit.jupiter.api.Test

class DslProjectionTest {

    @Test
    internal fun `test to see if we can generated the dsl out of the event stream`() = pokerTableTest {
        given {
            withPlayers()
            startGame(DeckMother().deckOfHearts())
        }

        val dsl = DslProjection().dsl(DummyEventStore(this.eventStore.given.toMutableList() /* todo fixme */))

        expect(dsl).toBe(
        """
            withPlayers()
            startGame(TEN of HEARTS,ACE of HEARTS,KING of HEARTS,QUEEN of HEARTS,NINE of HEARTS,EIGHT of HEARTS,SEVEN of HEARTS,SIX of HEARTS,FIVE of HEARTS)
        """.trimIndent())
    }
}