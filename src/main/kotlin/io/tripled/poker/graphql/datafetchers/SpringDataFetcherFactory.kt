package io.tripled.poker.graphql.datafetchers

import com.expedia.graphql.extensions.deepName
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.DataFetcherFactoryEnvironment
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.stereotype.Component

@Component
class SpringDataFetcherFactory(private val beanFactory: BeanFactory): DataFetcherFactory<Any>, BeanFactoryAware {

    override fun setBeanFactory(beanFactory: BeanFactory) = Unit

    @Suppress("UnsafeCast")
    override fun get(environment: DataFetcherFactoryEnvironment?): DataFetcher<Any> {
        //Strip out possible `Input` and `!` suffixes added to by the SchemaGenerator
        val targetedTypeName = environment?.fieldDefinition?.type?.deepName?.removeSuffix("!")?.removeSuffix("Input")
        return beanFactory.getBean("${targetedTypeName}DataFetcher") as DataFetcher<Any>
    }
}
