package io.tripled.poker.graphql

import com.expedia.graphql.DirectiveWiringHelper
import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import com.expedia.graphql.sample.directives.DirectiveWiringFactory
import com.expedia.graphql.sample.directives.LowercaseDirectiveWiring
import com.expedia.graphql.toSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import graphql.execution.AsyncExecutionStrategy
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import graphql.servlet.*
import io.tripled.poker.graphql.context.MyGraphQLContextBuilder
import io.tripled.poker.graphql.datafetchers.CustomDataFetcherFactoryProvider
import io.tripled.poker.graphql.datafetchers.SpringDataFetcherFactory
import io.tripled.poker.graphql.exceptions.CustomDataFetcherExceptionHandler
import io.tripled.poker.graphql.extension.CustomSchemaGeneratorHooks
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.servlet.http.HttpServlet
import javax.validation.Validator

@Configuration
class CustomGraphqlConfiguration {
    private val logger = LoggerFactory.getLogger(CustomGraphqlConfiguration::class.java)

    @Bean
    fun wiringFactory() = DirectiveWiringFactory()

    @Bean
    fun hooks(validator: Validator, wiringFactory: DirectiveWiringFactory) =
            CustomSchemaGeneratorHooks(validator, DirectiveWiringHelper(wiringFactory, mapOf("lowercase" to LowercaseDirectiveWiring())))

    @Bean
    fun dataFetcherFactoryProvider(springDataFetcherFactory: SpringDataFetcherFactory, hooks: SchemaGeneratorHooks) =
            CustomDataFetcherFactoryProvider(springDataFetcherFactory, hooks)

    @Bean
    fun schemaConfig(hooks: SchemaGeneratorHooks, dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider): SchemaGeneratorConfig = SchemaGeneratorConfig(
            supportedPackages = listOf("com.expedia"),
            hooks = hooks,
            dataFetcherFactoryProvider = dataFetcherFactoryProvider
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