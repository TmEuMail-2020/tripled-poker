package io.tripled.poker

import io.tripled.poker.api.TableUseCases
import io.tripled.poker.domain.Deck
import io.tripled.poker.domain.DummyDeck
import io.tripled.poker.eventsourcing.EventStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun deck() = DummyDeck()

    @Bean
    fun tableService(eventStore: EventStore, deck: Deck) = TableUseCases(eventStore, deck)
}