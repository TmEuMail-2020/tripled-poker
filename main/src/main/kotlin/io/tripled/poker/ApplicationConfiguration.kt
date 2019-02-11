package io.tripled.poker

import io.tripled.poker.api.TableUseCaseToRenameLater
import io.tripled.poker.eventsourcing.EventStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun tableService(eventStore: EventStore) = TableUseCaseToRenameLater(eventStore)
}