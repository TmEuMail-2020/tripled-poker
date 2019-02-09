package io.tripled.poker.infra.eventsourcing.mongostore

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.tripled.poker.eventsourcing.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

data class PersistedEvent<AggregateId, Payload>(@Id val eventId: UUID = UUID.randomUUID(),
                                                val aggregateId: AggregateId,
                                                @JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@payloadclass")
                                                val payload: Payload)

@Repository
class EventStore(val eventRepo: MongoEventRepository) : io.tripled.poker.eventsourcing.EventStore{
    override fun save(id: Any, event: Any){
        eventRepo.save(PersistedEvent(aggregateId = id.toString() as Any, payload = event))
    }

    override fun findById(id: Any): List<Any> = eventRepo.findByAggregateId(id.toString()).map { it.payload }.toList()
}

interface MongoEventRepository : MongoRepository<PersistedEvent<Any, Any>, UUID> {
    fun findByAggregateId(aggregateId: Any): List<PersistedEvent<Any, Any>>
}

@RestController
internal class TestEventPersistence(val eventStore: EventStore,
                                    val eventRepo: MongoEventRepository) {

    @GetMapping("/api/events/create")
    fun createEvents(){
        eventStore.save(BusinessId("100"), TestCreated("first name"))
        eventStore.save(BusinessId("100"), TestUpdated("name updated"))
    }

    @GetMapping("/api/events/print")
    fun printEvents() = eventStore.findById(BusinessId("100"))


    @GetMapping("/api/events/printAll")
    fun printAllEvents() = eventRepo.findAll()
}