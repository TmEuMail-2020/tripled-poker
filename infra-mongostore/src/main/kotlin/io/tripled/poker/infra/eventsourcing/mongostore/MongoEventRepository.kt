package io.tripled.poker.infra.eventsourcing.mongostore

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.tripled.poker.domain.Event
import io.tripled.poker.projection.DslProjection
import io.tripled.poker.vocabulary.GameId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

data class PersistedEvent<AggregateId, Payload>(@Id val eventId: UUID = UUID.randomUUID(),
                                                val aggregateId: AggregateId,
                                                @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@payloadclass")
                                                val payload: Payload)

@Repository
class EventStore(val eventRepo: MongoEventRepository) : io.tripled.poker.eventsourcing.EventStore {
    override fun append(id: Any, events: List<Event>) {
        val persistedEvents = events.map {
            PersistedEvent(aggregateId = id.toString() as Any, payload = it)
        }
        eventRepo.saveAll(persistedEvents)
    }

    override fun findById(id: Any): List<Event> = eventRepo.findByAggregateId(id.toString()).map { it.payload }.toList()

    interface MongoEventRepository : MongoRepository<PersistedEvent<Any, Event>, UUID> {
        fun findByAggregateId(aggregateId: Any): List<PersistedEvent<Any, Event>>
    }
}

@RestController
class EventController(private val eventRepo: EventStore.MongoEventRepository,
                      private val eventStore: io.tripled.poker.eventsourcing.EventStore){
    @GetMapping("/events/{eventId}")
    fun events(@PathVariable eventId: Any) = eventRepo.findByAggregateId(eventId)

    @GetMapping("/dsl/{gameId}")
    fun gameDsl(@PathVariable gameId: GameId) = DslProjection(eventStore).dsl(gameId)
}