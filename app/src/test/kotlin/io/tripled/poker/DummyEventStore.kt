package io.tripled.poker

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore

class DummyEventStore(private val _newEvents: MutableList<Event> = mutableListOf()) : EventStore {
    var given: List<Event> = listOf()

    fun given(pokerBuilder: EventBuilder.() -> Unit) {
        val builder = EventBuilder()
        builder.pokerBuilder()
        given = builder.events
    }

    override fun save(id: Any, events: List<Event>) {
        _newEvents.addAll(events)
    }

    override fun findById(id: Any): List<Event> {
        return given + _newEvents
    }

    val newEvents get() = _newEvents.toList()

    fun contains(element: Event) = this._newEvents.contains(element)

    class EventBuilder {
        val events = mutableListOf<Event>()

        fun playersJoin(vararg players: PlayerId) {
            events += players.map { PlayerJoinedTable(it) }
        }

        fun startGame(cardsInDeck: List<Card>, vararg playerHands: Pair<PlayerId, Hand>) {
            val playerIds = playerHands.map { p -> p.first }.toTypedArray()
            playersJoin(*playerIds)
            events += GameStarted(playerIds.toList())
            events += HandsAreDealt(cardsInDeck, mapOf(*playerHands))
        }
    }
}