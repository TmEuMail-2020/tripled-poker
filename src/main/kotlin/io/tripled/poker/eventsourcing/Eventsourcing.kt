package io.tripled.poker.eventsourcing

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

data class PersistedEvent<AggregateId, Payload>(@Id val eventId: UUID = UUID.randomUUID(),
                                                @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@idclass")
                                                val id: AggregateId,
                                                @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@payloadclass")
                                                val payload: Payload)

data class BusinessId(val id: String)
data class TestCreated(val name: String)
data class TestUpdated(val name: String)

interface EventRepository : MongoRepository<PersistedEvent<Any, Any>, UUID>

@RestController
internal class TestEventPersistence(val eventRepository: EventRepository) {

    @GetMapping("/api/events/create")
    fun createEvents(){
        val persistedEvent: PersistedEvent<Any, Any> = PersistedEvent(id = BusinessId("100"), payload = TestCreated("first name"))
        eventRepository.save(persistedEvent)
        val persistedEvent2: PersistedEvent<Any, Any> = PersistedEvent(id = BusinessId("100"), payload = TestUpdated("name updated"))
        eventRepository.save(persistedEvent2)
    }

    @GetMapping("/api/events/print")
    fun printEvents(): MutableList<PersistedEvent<Any, Any>> = eventRepository.findAll()

}