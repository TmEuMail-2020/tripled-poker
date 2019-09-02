package io.tripled.poker.projection

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore

class DslProjection {

    fun dsl(eventStore: EventStore): String {
        val tableEvents = mergeTableAndActiveGameStream(eventStore.findById(1), eventStore)

        val cards = projectCards(tableEvents)

        return cards
    }

    private fun projectCards(tableEvents: List<Event>) =
        tableEvents
                .filterEvents<HandsAreDealt>()
                .map {
                    it -> "withCards(${cards(it.cardsInDeck)})"
                }.first()

    private fun cards(cardsInDeck: List<Card>)
            = cardsInDeck.map {
                card -> card.mapToDsl()
            }.joinToString(",")

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