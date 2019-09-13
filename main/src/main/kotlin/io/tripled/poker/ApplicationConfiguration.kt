package io.tripled.poker

import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.projection.ActiveGames
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import kotlin.collections.HashMap

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
    fun gameUseCases(eventStore: EventStore, eventPublisher: EventPublisher, activeGames: ActiveGames)
            = GameUseCases(eventStore, eventPublisher, activeGames, { ShuffledDeck() })

    @Bean
    fun activeGames() = object : ActiveGames {
        private val activeGames = HashMap<TableId, GameId>()
        override fun activeGame(tableId: TableId): GameId = activeGames[tableId]!!

        override fun save(tableId: TableId, gameId: GameId){
            activeGames[tableId] = gameId
        }
    }
}

class ConcreteEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) : EventPublisher {
    override fun publish(id: Any, events: List<Event>) {
        events.forEach { event ->
            applicationEventPublisher.publishEvent(event)
        }
    }
}
