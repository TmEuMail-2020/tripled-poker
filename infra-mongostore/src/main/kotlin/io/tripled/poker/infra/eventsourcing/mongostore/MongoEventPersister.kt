package io.tripled.poker.infra.eventsourcing.mongostore

import io.tripled.poker.eventpublishing.DomainEvents
import org.springframework.stereotype.Component

@Component
class MongoEventPersister(val eventStore: EventStore) {

    //@EventListener
    fun domainEvents(domainEvents: DomainEvents) = eventStore.append(domainEvents.id, domainEvents.events)
}