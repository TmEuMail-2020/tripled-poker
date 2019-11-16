package io.tripled.poker.infra.eventsourcing

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(considerNestedRepositories = true)
class MongoConfiguration {
}