package io.tripled.poker.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import graphql.Assert.assertNotNull
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan


@GraphQLTest
@ComponentScan
class GraphqlQueryIntegrationTest(
    @Autowired val graphQLTestTemplate: GraphQLTestTemplate
) {

    @Test
    fun contextLoads() {
        val response = graphQLTestTemplate.postForResource("requests/joinTableMutation.graphql")

        assertNotNull(response)
        assertThat(response.isOk, equalTo(true))
        assertEquals("yves", response.get("$.data.joinTable.players[0].name"))
    }

}
