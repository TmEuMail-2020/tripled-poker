package io.tripled.poker.domain

data class PlayerJoinedTable(val name: String)
data class RoundStarted(val noop: String = "")

class Table(events: List<Any>) {

    private val playerCount: Int

    init {
        playerCount = events.size
    }

    fun join(name: String) = listOf<Any>(PlayerJoinedTable(name))

    fun startRound() =
            if (playerCount > 1) listOf<Any>(RoundStarted())
            else listOf<Any>()


}