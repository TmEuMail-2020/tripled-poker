package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit.*
import io.tripled.poker.api.response.Value.*

val suitedConnectors = Hand(EIGHT of DIAMOND, NINE of DIAMOND)
val suitedAceKing = Hand(KING of CLUB, ACE of CLUB)

internal fun Card.mapToCard() = io.tripled.poker.api.response.Card(this.suit, this.value)