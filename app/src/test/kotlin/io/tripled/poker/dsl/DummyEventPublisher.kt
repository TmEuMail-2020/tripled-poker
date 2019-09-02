package io.tripled.poker.dsl

import io.tripled.poker.domain.Event
import io.tripled.poker.domain.EventPublisher

internal class DummyEventPublisher : EventPublisher {
    override fun publish(id: Any, events: List<Event>) = Unit
}