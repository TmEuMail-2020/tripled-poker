package io.tripled.poker

import io.sentry.spring.SentryExceptionResolver
import io.sentry.spring.SentryServletContextInitializer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerExceptionResolver

@Configuration
class SentryErrorReporting {
    @Bean
    fun sentryExceptionResolver(): HandlerExceptionResolver {
        return SentryExceptionResolver()
    }

    @Bean
    fun sentryServletContextInitializer(): ServletContextInitializer {
        return SentryServletContextInitializer()
    }

    @RestController
    class ErrorTest {
        @GetMapping("generateError")
        fun generateError(): Nothing = throw RuntimeException("errors happening")
    }
}