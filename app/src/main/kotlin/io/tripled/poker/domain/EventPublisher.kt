package io.tripled.poker.domain


interface EventPublisher {
    fun publish(id: Any, events: List<Event>)
}