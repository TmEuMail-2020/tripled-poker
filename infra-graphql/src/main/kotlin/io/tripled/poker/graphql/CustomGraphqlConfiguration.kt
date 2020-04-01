package io.tripled.poker.graphql

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.toSchema
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
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
                        .includeSchemaDefinition(true)
        ).print(schema)
        )
        return schema
    }

    @Bean
    fun assumeUser() = AssumeUser()
}