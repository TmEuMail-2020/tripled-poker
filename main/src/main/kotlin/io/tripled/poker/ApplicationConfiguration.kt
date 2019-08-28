package io.tripled.poker

import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.Event
import io.tripled.poker.domain.EventPublisher
import io.tripled.poker.domain.GameId
import io.tripled.poker.domain.ShuffledDeck
import io.tripled.poker.eventsourcing.EventStore
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class ApplicationConfiguration {

    @Bean
    fun eventPublisher(applicationEventPublisher: ApplicationEventPublisher)
            = ConcreteEventPublisher(applicationEventPublisher)

    @Bean
    fun tableUseCases(eventStore: EventStore, eventPublisher: EventPublisher, gameUseCases: GameService)
            = TableUseCases(eventStore, gameUseCases, eventPublisher) {
                UUID.randomUUID().toString()
            }

    @Bean
    fun gameUseCases(eventStore: EventStore, eventPublisher: EventPublisher)
            = GameUseCases(eventStore, { ShuffledDeck() }, eventPublisher)
}

class ConcreteEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) : EventPublisher {
    override fun publish(id: Any, events: List<Event>) {
        events.forEach { event ->
            applicationEventPublisher.publishEvent(event)
        }
    }

}
