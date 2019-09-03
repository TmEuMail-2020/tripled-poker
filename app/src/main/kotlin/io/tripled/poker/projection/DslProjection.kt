package io.tripled.poker.projection

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore

class DslProjection {

    fun dsl(eventStore: EventStore): String {
        val tableEvents = mergeTableAndActiveGameStream(eventStore.findById(1), eventStore)

        val withPlayers = withPlayers(tableEvents)
        val startGame = startGame(tableEvents)
        val preflop = preflop(tableEvents)

        return listOf(
                withPlayers,
                startGame,
                preflop
            ).joinToString("\n")
    }

    private fun withPlayers(tableEvents: List<Event>) =
            "withPlayers(${tableEvents
                    .filterEvents<PlayerJoinedTable>()
                    .map {
                        it -> it.name
                    }.joinToString(", ")})"

    private fun startGame(tableEvents: List<Event>) =
            "startGame(${tableEvents
                .filterEvents<GameStarted>()
                .map {
                    it -> it.cardsInDeck.map {
                        card -> card.mapToDsl()
                    }.joinToString(",")
                }.first()})"

    private fun preflop(tableEvents: List<Event>) =
            """preflop(
${tableEvents.filterEvents<HandsAreDealt>()
                    .map {
                        it -> it.hands.map { pv ->
                        "    ${pv.key} to ((${pv.value.card1.mapToDsl()}) and (${pv.value.card2.mapToDsl()}))"
                        }.joinToString(",\n")
                    }.first()}
) {
}""".trimIndent()


    private fun mergeTableAndActiveGameStream(tableEvents: List<Event>, eventStore: EventStore): List<Event> {
        tableEvents
                .filterEvents<GameStarted>()
                .lastOrNull()?.apply {
                    val currentlyActiveGameEvents = eventStore.findById(gameId)
                    return tableEvents.union(currentlyActiveGameEvents).toList()
                }
        return tableEvents
    }

    private fun Card.mapToDsl() = "$value of $suit"

}