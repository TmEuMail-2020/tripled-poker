package io.tripled.poker.eventsourcing

import io.tripled.poker.domain.Event

interface EventStore {
    fun append(id: Any, events: List<Event>)

    fun findById(id: Any): List<Event>
}