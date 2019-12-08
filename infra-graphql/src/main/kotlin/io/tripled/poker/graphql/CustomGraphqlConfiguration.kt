package io.tripled.poker.graphql

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.toSchema
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker

@Configuration
@EnableWebSocket
class CustomGraphqlConfiguration {
    private val logger = LoggerFactory.getLogger(CustomGraphqlConfiguration::class.java)

    @Bean
    fun schemaConfig(): SchemaGeneratorConfig = SchemaGeneratorConfig(
            supportedPackages = listOf(
                    "io.tripled.poker.graphql.query",
                    "io.tripled.poker.graphql.mutation",
                    "io.tripled.poker.graphql.subscription",
                    "io.tripled.poker.vocabulary",
                    "io.tripled.poker.app.api.response"
            )
    )

    @Bean
    fun schema(
            queries: List<Query>,
            mutations: List<Mutation>,
            subscriptions: List<Subscription>,
            schemaConfig: SchemaGeneratorConfig
    ): GraphQLSchema {
        fun List<Any>.toTopLevelObjectDefs() = this.map {
            TopLevelObject(it)
        }

        val schema = toSchema(
                queries = queries.toTopLevelObjectDefs(),
                mutations = mutations.toTopLevelObjectDefs(),
                subscriptions = subscriptions.toTopLevelObjectDefs(),
                config = schemaConfig
        )
        logger.info(SchemaPrinter(
                SchemaPrinter.Options.defaultOptions()
                        .includeScalarTypes(true)
                        .includeExtendedScalarTypes(true)
                        .includeSchemaDefintion(true)
        ).print(schema)
        )
        return schema
    }

    @Bean
    fun assumeUser() = AssumeUser()
}