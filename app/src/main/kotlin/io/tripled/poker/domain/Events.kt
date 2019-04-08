package io.tripled.poker.domain

interface Event
data class PlayerJoinedTable(val name: String) : Event
data class RoundStarted(val noop: String = "") : Event
data class CardsAreDealt(val hands: Map<PlayerId, Hand>) : Event
data class PlayerWonRound(val name: PlayerId) : Event
data class FlopIsTurned(val  card1: Card, val card2: Card, val card3: Card) : Event
data class TurnIsTurned(val  card: Card) : Event
