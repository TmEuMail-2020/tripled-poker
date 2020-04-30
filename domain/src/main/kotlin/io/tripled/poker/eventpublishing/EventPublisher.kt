package io.tripled.poker.eventpublishing

import io.tripled.poker.domain.Event

data class DomainEvents(val id: Any, val events: List<Event>)

interface EventPublisher {
    fun publish(id: Any, events: List<Event>)
}