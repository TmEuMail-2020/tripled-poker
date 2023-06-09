package io.tripled.poker.domain

interface Event
data class PlayerJoinedTable(val name: String) : Event
data class GameStarted(val remainingCards: List<Card>) : Event
data class CardsAreDealt(val hands: Map<PlayerId, Hand>) : Event
data class PlayerCalled(val name: PlayerId) : Event
data class PlayerWonGame(val name: PlayerId) : Event
data class FlopIsTurned(val  card1: Card, val card2: Card, val card3: Card) : Event
data class TurnIsTurned(val  card: Card) : Event
data class RiverIsTurned(val  card: Card) : Event
