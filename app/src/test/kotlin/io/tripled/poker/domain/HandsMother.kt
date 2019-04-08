package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit
import io.tripled.poker.api.response.Value

val suitedConnectors = Hand(Card(Suit.DIAMOND, Value.EIGHT), Card(Suit.DIAMOND, Value.NINE))
val suitedAceKing = Hand(Card(Suit.CLUB, Value.KING), Card(Suit.CLUB, Value.ACE))

internal fun io.tripled.poker.domain.Card.mapToCard() = io.tripled.poker.api.response.Card(this.suit, this.value)