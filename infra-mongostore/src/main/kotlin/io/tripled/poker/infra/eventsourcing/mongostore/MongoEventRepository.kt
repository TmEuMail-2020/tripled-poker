package io.tripled.poker.infra.eventsourcing.mongostore

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

data class PersistedEvent<AggregateId, Payload>(@Id val eventId: UUID = UUID.randomUUID(),
                                                val aggregateId: AggregateId,
                                                @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@payloadclass")
                                                val payload: Payload)

@Repository
class EventStore(val eventRepo: MongoEventRepository) : io.tripled.poker.eventsourcing.EventStore {
    override fun save(id: Any, events: List<Any>) {
        val persistedEvents = events.map {
            PersistedEvent(aggregateId = id.toString() as Any, payload = it)
        }
        eventRepo.saveAll(persistedEvents)
    }

    override fun findById(id: Any): List<Any> = eventRepo.findByAggregateId(id.toString()).map { it.payload }.toList()
}

interface MongoEventRepository : MongoRepository<PersistedEvent<Any, Any>, UUID> {
    fun findByAggregateId(aggregateId: Any): List<PersistedEvent<Any, Any>>
}
