package io.tripled.poker.dsl

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId

class DummyEventStore(var given: MutableMap<Any, List<Event>> = mutableMapOf()) : EventStore {
    private val newlyDispatchedEvents = given.toMutableMap()

    fun given(pokerBuilder: EventBuilder.() -> Unit) {
        val builder = EventBuilder()
        builder.pokerBuilder()
        given = builder.events
    }

    override fun save(id: Any, events: List<Event>) {
        newlyDispatchedEvents.append(id, events)
    }

    override fun findById(id: Any): List<Event> {
        val givenEvents = given[id] ?: listOf()
        val newEvents = newlyDispatchedEvents[id] ?: listOf()
        return givenEvents + newEvents
    }

    val newEvents get() = newlyDispatchedEvents.flatMap { it.value }.toList()

    fun contains(element: Event) = this.newlyDispatchedEvents.contains(element)

    class EventBuilder {
        val events = mutableMapOf<Any, List<Event>>()

        private fun playersJoin(vararg players: PlayerId) {
            events.append("id", players.map { PlayerJoinedTable(it) })
        }

        fun startGame(gameId: GameId, cardsInDeck: List<Card>, vararg playerHands: Pair<PlayerId, Hand>) {
            val playerIds = playerHands.map { p -> p.first }.toTypedArray()
            playersJoin(*playerIds)
            events.putIfAbsent(gameId, mutableListOf())
            events["1"] = events["1"]!! + GameCreated(gameId, playerIds.toList())
            events[gameId] = events[gameId]!! + GameCreated(gameId, playerIds.toList())
            events[gameId] = events[gameId]!! + HandsAreDealt(mapOf(*playerHands))
        }
    }
}

fun <Id,Value> MutableMap<Id, List<Value>>.append(id: Id, items: List<Value>) =
        this.merge(id, items) { t, u -> t + u }
