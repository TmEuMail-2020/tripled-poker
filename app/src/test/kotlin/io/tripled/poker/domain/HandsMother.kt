package io.tripled.poker.domain

import io.tripled.poker.api.response.Suit.*
import io.tripled.poker.api.response.Value.*

val suitedConnectors = (EIGHT of DIAMONDS) and ( NINE of DIAMONDS)
val suitedAceKing = (KING of CLUBS) and (ACE of CLUBS)

internal fun Card.mapToCard() = io.tripled.poker.api.response.Card(this.suit, this.value)