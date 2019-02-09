package io.tripled.poker.graphql

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.toSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import graphql.execution.AsyncExecutionStrategy
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import graphql.servlet.DefaultExecutionStrategyProvider
import graphql.servlet.GraphQLErrorHandler
import graphql.servlet.GraphQLInvocationInputFactory
import graphql.servlet.GraphQLObjectMapper
import graphql.servlet.GraphQLQueryInvoker
import graphql.servlet.ObjectMapperConfigurer
import io.tripled.poker.graphql.context.MyGraphQLContextBuilder
import io.tripled.poker.graphql.exceptions.CustomDataFetcherExceptionHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CustomGraphqlConfiguration {
    private val logger = LoggerFactory.getLogger(CustomGraphqlConfiguration::class.java)

    @Bean
    fun schemaConfig(): SchemaGeneratorConfig = SchemaGeneratorConfig(
            supportedPackages = listOf("io.tripled.poker.graphql.test")
    )

    @Bean
    fun schema(
            queries: List<Query>,
            mutations: List<Mutation>,
            schemaConfig: SchemaGeneratorConfig
    ): GraphQLSchema {
        fun List<Any>.toTopLevelObjectDefs() = this.map {
            TopLevelObject(it)
        }

        val schema = toSchema(
                queries = queries.toTopLevelObjectDefs(),
                mutations = mutations.toTopLevelObjectDefs(),
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
    fun contextBuilder() = MyGraphQLContextBuilder()

    @Bean
    fun graphQLInvocationInputFactory(
            schema: GraphQLSchema,
            contextBuilder: MyGraphQLContextBuilder
    ): GraphQLInvocationInputFactory = GraphQLInvocationInputFactory.newBuilder(schema)
            .withGraphQLContextBuilder(contextBuilder)
            .build()

    @Bean
    fun graphQLQueryInvoker(): GraphQLQueryInvoker {
        val exceptionHandler = CustomDataFetcherExceptionHandler()
        val executionStrategyProvider = DefaultExecutionStrategyProvider(AsyncExecutionStrategy(exceptionHandler))

        return GraphQLQueryInvoker.newBuilder()
                .withExecutionStrategyProvider(executionStrategyProvider)
                .build()
    }

    @Bean
    fun graphQLObjectMapper(): GraphQLObjectMapper = GraphQLObjectMapper.newBuilder()
            .withObjectMapperConfigurer(ObjectMapperConfigurer { it.registerModule(KotlinModule()) })
            .withGraphQLErrorHandler(GraphQLErrorHandler { it })
            .build()

}