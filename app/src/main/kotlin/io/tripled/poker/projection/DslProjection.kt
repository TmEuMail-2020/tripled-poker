package io.tripled.poker.projection

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.vocabulary.GameId

class DslProjection(private val eventStore: EventStore) {

    fun dsl(): String {
        val tableEvents = eventStore.findById("1")
        tableEvents
                .filterEvents<GameCreated>()
                .lastOrNull()?.apply {
                    return dsl(mergeTableAndActiveGameStream(tableEvents, eventStore, gameId))
                }
        return dsl(tableEvents)
    }

    fun dsl(gameId: GameId): String {
        val tableEvents = eventStore.findById("1")
        return dsl(mergeTableAndActiveGameStream(tableEvents, eventStore, gameId))
    }

    private fun dsl(tableEvents: List<Event>): String {
        val withPlayers = tableEvents.ifContaining<PlayerJoinedTable> { withPlayers(tableEvents) }
        val startGame = tableEvents.ifContaining<GameCreated> { startGame(tableEvents) }
        val preflop = tableEvents.ifContaining<HandsAreDealt> { preflop(tableEvents) }
        val flop = tableEvents.ifContaining<FlopIsTurned> { flop(tableEvents) }
        val turn = tableEvents.ifContaining<TurnIsTurned> { turn(tableEvents) }
        val river = tableEvents.ifContaining<RiverIsTurned> { river(tableEvents) }
        val expectedWinner = tableEvents.ifContaining<PlayerWonGame> { expectedWinner(tableEvents) }

        return listOf(
                withPlayers,
                startGame,
                preflop,
                flop,
                turn,
                river,
                expectedWinner
        ).filter { it.isNotBlank() }.joinToString("\n")
    }

    private fun expectedWinner(tableEvents: List<Event>) =
            tableEvents.filterEvents<PlayerWonGame>().map { "expectWinner(${it.name})" }.first()

    private fun mergeTableAndActiveGameStream(tableEvents: List<Event>, eventStore: EventStore, gameId: GameId): List<Event> {
        val currentlyActiveGameEvents = eventStore.findById(gameId)
        return tableEvents.union(currentlyActiveGameEvents).toList()
    }

    private fun withPlayers(tableEvents: List<Event>) =
            "withPlayers(${tableEvents
                    .filterEvents<PlayerJoinedTable>()
                    .map {
                        it -> it.name
                    }.joinToString(", ")})"

    private fun startGame(tableEvents: List<Event>) =
            "startGame(listOf(${tableEvents
                .filterEvents<GameStarted>()
                .map {
                    it -> it.cardsInDeck.map {
                        card -> card.mapToDsl()
                    }.joinToString(",")
                }.first()}))"

    private fun preflop(tableEvents: List<Event>) =
            """preflop(
${`players and their cards`(tableEvents)}
) {
${`preflop actions`(tableEvents)}
}""".trimIndent()

    private fun `preflop actions`(tableAndGameEvents: List<Event>): String
        = tableAndGameEvents.ifContaining<GameStarted> {
        tableAndGameEvents.subList(0, tableAndGameEvents.indexOf(tableAndGameEvents.filterEvents<GameStarted>()[0])).map {
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
                    }.firstOrNull()}
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

    private fun turn(tableEvents: List<Event>) =
            """turn(${tableEvents.filterEvents<TurnIsTurned>()
                    .map {
                        it.card.mapToDsl()
                    }.firstOrNull()}) {
${`flop actions`(tableEvents)}
}""".trimIndent()

    private fun river(tableEvents: List<Event>) =
            """river(${tableEvents.filterEvents<RiverIsTurned>()
                    .map {
                        it.card.mapToDsl()
                    }.firstOrNull()}) {
${`flop actions`(tableEvents)}
}""".trimIndent()

    private fun Card.mapToDsl() = "$value of $suit"

}