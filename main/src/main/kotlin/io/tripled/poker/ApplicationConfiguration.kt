package io.tripled.poker

import io.tripled.poker.api.TableService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun tableService()= TableService()
}