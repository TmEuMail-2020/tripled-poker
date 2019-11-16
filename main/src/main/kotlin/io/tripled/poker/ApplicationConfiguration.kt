package io.tripled.poker

import io.tripled.poker.app.GameUseCases
import io.tripled.poker.app.TableUseCases
import io.tripled.poker.app.api.GameService
import io.tripled.poker.domain.*
import io.tripled.poker.domain.cards.ShuffledDeck
import io.tripled.poker.domain.game.GameRepository
import io.tripled.poker.domain.table.GameCreated
import io.tripled.poker.domain.table.TableRepository
import io.tripled.poker.eventpublishing.DomainEvents
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.infra.eventsourcing.MongoConfiguration
import io.tripled.poker.projection.ActiveGames
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId
import io.tripled.poker.vocabulary.TableId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.HashMap

@Configuration
@Import(value = [MongoConfiguration::class])
class ApplicationConfiguration {

    @Bean
    fun eventPublisher(applicationEventPublisher: ApplicationEventPublisher)
            = ConcreteEventPublisher(applicationEventPublisher)

    @Bean
    fun tableUseCases(tableRepository: TableRepository, eventPublisher: EventPublisher, users: Users)
            = TableUseCases(tableRepository, eventPublisher, users) {
                UUID.randomUUID().toString()
            }

    @Bean
    fun gameUseCases(gameRepository: GameRepository, eventPublisher: EventPublisher, activeGames: ActiveGames, users: Users)
            = GameUseCases(gameRepository, eventPublisher, activeGames, users) { ShuffledDeck() }

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

            domainToApplicationEvents(event, id)
        }

        applicationEventPublisher.publishEvent(DomainEvents(id, events))
    }

    private fun domainToApplicationEvents(event: Event, id: Any) {
        when (event) {
            is GameCreated -> applicationEventPublisher.publishEvent(GameStartedEvent(id as TableId, event.gameId, event.players))
        }
    }
}

// POLICY
data class GameStartedEvent(val tableId: TableId, val gameId: GameId, val players: List<PlayerId>)

@Component
class Policy (private val gameUseCases: GameService) {
    @EventListener
    fun startGameAfterCreation(event: GameStartedEvent) = gameUseCases.startGame(event.tableId, event.gameId, event.players)
}