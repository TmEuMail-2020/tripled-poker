package io.tripled.poker.domain

import io.tripled.poker.vocabulary.Value.*
import io.tripled.poker.vocabulary.Suit.*

val suitedConnectors = (EIGHT of DIAMONDS) and ( NINE of DIAMONDS)
val suitedAceKing = (KING of CLUBS) and (ACE of CLUBS)

internal fun Card.mapToCard() = io.tripled.poker.vocabulary.Card(this.suit, this.value)