package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit.HEART
import io.tripled.poker.api.response.Value.*

class DeckMother {
    fun deckOfHearts(): List<Card> {
        return listOf(
                TEN of HEART,
                ACE of HEART,
                KING of HEART,
                QUEEN of HEART,
                NINE of HEART,
                EIGHT of HEART,
                SEVEN of HEART,
                SIX of HEART,
                FIVE of HEART
        )
    }
}
