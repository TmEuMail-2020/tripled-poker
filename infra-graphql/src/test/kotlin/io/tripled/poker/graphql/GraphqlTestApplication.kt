package io.tripled.poker.graphql

import io.tripled.poker.domain.User
import io.tripled.poker.domain.Users
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphqlTestApplication {
    @Bean
    fun dummyTableService(users: Users) = DummyTableService(users)

    @Bean
    fun dummyGameService() = DummyGameService()

    @Bean
    fun assumeUser() = object : Users {
        override val currentUser: User
            get() = User("yves")
    }
}