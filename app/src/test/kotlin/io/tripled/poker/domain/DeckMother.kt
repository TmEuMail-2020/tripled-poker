package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit.HEARTS
import io.tripled.poker.api.response.Value.*

class DeckMother {
    fun deckOfHearts(): List<Card> {
        return listOf(
                TEN of HEARTS,
                ACE of HEARTS,
                KING of HEARTS,
                QUEEN of HEARTS,
                NINE of HEARTS,
                EIGHT of HEARTS,
                SEVEN of HEARTS,
                SIX of HEARTS,
                FIVE of HEARTS
        )
    }
}
