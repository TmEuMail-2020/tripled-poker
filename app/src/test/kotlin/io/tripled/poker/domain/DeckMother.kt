package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value

class DeckMother {
    fun deckOfHearts(): List<Card> {
        return listOf(
                Card(Suit.HEART, Value.TEN),
                Card(Suit.HEART, Value.ACE),
                Card(Suit.HEART, Value.KING),
                Card(Suit.HEART, Value.QUEEN),
                Card(Suit.HEART, Value.NINE),
                Card(Suit.HEART, Value.EIGHT),
                Card(Suit.HEART, Value.SEVEN),
                Card(Suit.HEART, Value.SIX),
                Card(Suit.HEART, Value.FIVE)
        )
    }
}
