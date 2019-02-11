package io.tripled.poker.domain

data class PlayerJoinedTable(val name: String)
data class RoundStarted(val noop: String = "")
data class CardsAreDealt(val cards: Map<String, Card>)

data class Card(val noop: String = "")

class Table(events: List<Any>) {
    private val players = players(events)

    private fun players(events: List<Any>): List<String> {
        return events
                .filter { it is PlayerJoinedTable }
                .map { event ->
                    (event as PlayerJoinedTable).name
                }
    }

    fun join(name: String) = listOf<Any>(PlayerJoinedTable(name))

    fun startRound() =
            if (players.size > 1)
                listOf(
                    RoundStarted(),
                    CardsAreDealt(players.map { it to Card() }.toMap())
                )
            else listOf()


}