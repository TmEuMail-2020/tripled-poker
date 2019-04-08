package io.tripled.poker.domain

interface Event
data class PlayerJoinedTable(val name: String) : Event
data class RoundStarted(val noop: String = "") : Event
data class CardsAreDealt(val hands: Map<PlayerId, Hand>) : Event
data class PlayerWonRound(val name: PlayerId) : Event