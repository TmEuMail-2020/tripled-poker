package io.tripled.poker.eventpublishing

import io.tripled.poker.domain.Event

interface EventPublisher {
    fun publish(id: Any, events: List<Event>)
}