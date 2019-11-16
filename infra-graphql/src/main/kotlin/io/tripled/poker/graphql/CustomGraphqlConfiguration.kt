package io.tripled.poker.graphql

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.toSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import graphql.servlet.config.ObjectMapperConfigurer
import graphql.servlet.core.GraphQLErrorHandler
import graphql.servlet.core.GraphQLObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket

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
    fun graphQLObjectMapper(): GraphQLObjectMapper = GraphQLObjectMapper.newBuilder()
            .withObjectMapperConfigurer(ObjectMapperConfigurer { it.registerModule(KotlinModule()) })
            .withGraphQLErrorHandler(GraphQLErrorHandler { it })
            .build()

    @Bean
    fun assumeUser() = AssumeUser()
}