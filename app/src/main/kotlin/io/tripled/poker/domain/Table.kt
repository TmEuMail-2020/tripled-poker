package io.tripled.poker.domain

class Player(val name: String)

class Table {
    val players = mutableListOf<Player>()

    fun join(name: String): Int {
        players.add(Player(name))
        return 1
    }
}