package io.tripled.poker.graphql

import io.tripled.poker.domain.User
import io.tripled.poker.domain.Users
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GraphqlTestApplication {
    @Bean
    fun tableService(assumeUser: AssumeUser) = DummyTableService(assumeUser)

    @Bean
    fun assumeUser() = object : Users {
        override val currentUser: User
            get() = User("AssumedUser")
    }
}