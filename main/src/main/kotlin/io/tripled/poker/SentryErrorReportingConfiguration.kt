package io.tripled.poker

import io.sentry.spring.SentryExceptionResolver
import io.sentry.spring.SentryServletContextInitializer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SentryErrorReportingConfiguration {
    @Bean
    fun sentryExceptionResolver() = SentryExceptionResolver()

    @Bean
    @Suppress("FunctionMaxLength")
    fun sentryServletContextInitializer(): ServletContextInitializer = SentryServletContextInitializer()
}