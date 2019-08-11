package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit.*
import io.tripled.poker.api.response.Value.*

val suitedConnectors = Hand(EIGHT of DIAMONDS, NINE of DIAMONDS)
val suitedAceKing = Hand(KING of CLUBS, ACE of CLUBS)

internal fun Card.mapToCard() = io.tripled.poker.api.response.Card(this.suit, this.value)