package io.tripled.poker

import io.tripled.poker.eventsourcing.EventStore

class TestEventStore(val events: MutableList<Any> = mutableListOf()) : EventStore {
    override fun save(id: Any, events: List<Any>) {
        this.events.addAll(events)
    }


    override fun findById(id: Any): List<Any> {
        return events
    }
}