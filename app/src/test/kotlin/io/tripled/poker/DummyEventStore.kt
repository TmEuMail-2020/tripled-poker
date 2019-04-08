package io.tripled.poker

import io.tripled.poker.domain.Event
import io.tripled.poker.eventsourcing.EventStore

class DummyEventStore(private val _events: MutableList<Event> = mutableListOf()) : EventStore {
    override fun save(id: Any, events: List<Event>) {
        this._events.addAll(events)
    }

    override fun findById(id: Any): List<Event> {
        return _events
    }

    val events get() = _events.toList()

    fun contains(element: Event) = this._events.contains(element)
}