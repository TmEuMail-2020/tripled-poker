package io.tripled.poker.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GraphqlTestApplication {
    @Bean
    fun tableService(assumeUser: AssumeUser) = DummyTableService(assumeUser)
}