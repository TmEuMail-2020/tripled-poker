package io.tripled.poker.eventsourcing

import io.tripled.poker.domain.Event

interface EventStore {
    fun save(id: Any, event: Event)
    fun save(id: Any, events: List<Event>)

    fun findById(id: Any): List<Event>
}