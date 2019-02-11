package io.tripled.poker.api.response


data class Player(val name: String)
data class Table(val players: List<Player>)