package io.tripled.poker

import io.tripled.poker.domain.Event
import io.tripled.poker.eventsourcing.EventStore

class DummyEventStore(private val events: MutableList<Event> = mutableListOf()) : EventStore {
    override fun save(id: Any, events: List<Event>) {
        this.events.addAll(events)
    }

    override fun findById(id: Any): List<Event> {
        return events
    }

    fun contains(element: Event) = this.events.contains(element)
}