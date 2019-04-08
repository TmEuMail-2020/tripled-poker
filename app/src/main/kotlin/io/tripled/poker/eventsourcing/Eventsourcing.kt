package io.tripled.poker.eventsourcing

interface EventStore {
    fun save(id: Any, events: List<Any>)

    fun findById(id: Any): List<Any>
}