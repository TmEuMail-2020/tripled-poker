package io.tripled.poker.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import graphql.Assert.assertNotNull
import net.minidev.json.JSONArray
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import


@GraphQLTest
@Import(value = [GraphqlTestApplication::class])
@ComponentScan
class GraphqlQueryIntegrationTest(
        @Autowired val dummyTableService: DummyTableService,
        @Autowired val graphQLTestTemplate: GraphQLTestTemplate
) {

    private val joinTable = "requests/joinTableMutation.graphql"
    private val queryTable = "requests/queryTable.graphql"

    @BeforeEach
    internal fun setUp() {
        dummyTableService.clear()
    }

    @Test
    fun `join table mutation`() {
        val response = post(joinTable)

        assertNotNull(response)
        assertThat(response.isOk, equalTo(true))
        assertEquals("yves", response.get("$.data.joinTable.players[0].name"))
    }

    @Test
    fun `query empty table`() {
        val response = post(queryTable)

        assertNotNull(response)
        assertThat(response.isOk, equalTo(true))
        assertEquals(0, response.get("$.data.table.players", JSONArray::class.java).size)
    }

    private fun post(resource: String) = graphQLTestTemplate.postForResource(resource)

}
