package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.Event
import io.tripled.poker.domain.EventPublisher
import io.tripled.poker.domain.ShuffledDeck
import io.tripled.poker.eventsourcing.EventStore
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun eventPublisher(applicationEventPublisher: ApplicationEventPublisher) = ConcreteEventPublisher(applicationEventPublisher)

    @Bean
    fun tableService(eventStore: EventStore, eventPublisher: EventPublisher) = TableUseCases(eventStore, {ShuffledDeck()}, eventPublisher)
}

class ConcreteEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) : EventPublisher {
    override fun publish(id: Any, events: List<Event>) {
        events.forEach { event ->
            applicationEventPublisher.publishEvent(event)
        }
    }

}
