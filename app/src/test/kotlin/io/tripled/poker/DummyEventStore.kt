package io.tripled.poker

import io.tripled.poker.domain.Event
import io.tripled.poker.eventsourcing.EventStore

class DummyEventStore(private val _newEvents: MutableList<Event> = mutableListOf()) : EventStore {
    var given: List<Event> = listOf()

    override fun save(id: Any, events: List<Event>) {
        _newEvents.addAll(events)
    }

    override fun findById(id: Any): List<Event> {
        return given + _newEvents
    }

    val newEvents get() = _newEvents.toList()

    fun contains(element: Event) = this._newEvents.contains(element)
}