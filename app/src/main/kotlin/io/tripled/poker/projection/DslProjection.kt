package io.tripled.poker.projection

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore

class DslProjection {

    fun dsl(eventStore: EventStore): String {
        val tableEvents = mergeTableAndActiveGameStream(eventStore.findById(1), eventStore)

        val withPlayers = withPlayers(tableEvents)
        val startGame = startGame(tableEvents)
        val preflop = preflop(tableEvents)
        val flop = flop(tableEvents)

        return listOf(
                withPlayers,
                startGame,
                preflop,
                flop
            ).joinToString("\n")
    }

    private fun mergeTableAndActiveGameStream(tableEvents: List<Event>, eventStore: EventStore): List<Event> {
        tableEvents
                .filterEvents<GameStarted>()
                .lastOrNull()?.apply {
                    val currentlyActiveGameEvents = eventStore.findById(gameId)
                    return tableEvents.union(currentlyActiveGameEvents).toList()
                }
        return tableEvents
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
${`players and their cards`(tableEvents)}
) {
${`preflop actions`(tableEvents)}
}""".trimIndent()

    private fun `preflop actions`(tableEvents: List<Event>): String {
        return tableEvents.subList(0, tableEvents.indexOf(tableEvents.filterEvents<RoundCompleted>()[0])).map {
            when (it) {
                is PlayerChecked -> "    ${it.name}.checks()"
                else -> ""
            }
        }.filter { it.isNotBlank() }.joinToString("\n")
    }

    private fun `players and their cards`(tableEvents: List<Event>): String {
        return tableEvents.filterEvents<HandsAreDealt>()
                .map {
                    it.hands.map { pv ->
                        "    ${pv.key} to ((${pv.value.card1.mapToDsl()}) and (${pv.value.card2.mapToDsl()}))"
                    }.joinToString(",\n")
                }.first()
    }

    private fun flop(tableEvents: List<Event>) =
            """flop(${tableEvents.filterEvents<FlopIsTurned>()
                    .map { 
                        listOf(it.card1.mapToDsl(), 
                                "        " + it.card2.mapToDsl(), 
                                "        " + it.card3.mapToDsl())
                                .joinToString(",\n") 
                    }.first()}
) {
${`flop actions`(tableEvents)}
}""".trimIndent()

    private fun `flop actions`(tableEvents: List<Event>): String {
        return tableEvents.map {
            when (it) {
                is PlayerChecked -> "    ${it.name}.checks()"
                else -> ""
            }
        }.filter { it.isNotBlank() }.joinToString("\n")
    }

    private fun Card.mapToDsl() = "$value of $suit"

}