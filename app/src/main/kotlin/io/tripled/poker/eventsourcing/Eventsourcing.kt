package io.tripled.poker.eventsourcing

// example events
data class BusinessId(val id: String)
data class TestCreated(val name: String)
data class TestUpdated(val name: String)

interface EventStore {
    fun save(id: Any, events: List<Any>)

    fun findById(id: Any): List<Any>
}