package io.tripled.poker.domain

data class PlayerJoinedTable(val name: String)
data class RoundStarted(val noop: String = "")
data class CardsAreDealt(val hands: Map<PlayerId, Hand>)
data class PlayerWonRound(val name: PlayerId)