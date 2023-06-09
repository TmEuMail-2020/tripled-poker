package io.tripled.poker

import com.graphql.spring.boot.test.GraphQLTestTemplate
import net.minidev.json.JSONObject
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PokerScenarios (
	@Autowired val graphQLTestTemplate: GraphQLTestTemplate){

	@Test
	fun `three players join table`() {
		request("requests/jan-joins.graphql")
		request("requests/jef-joins.graphql")
		request("requests/jos-joins.graphql")

		expectTable("""
							{
							  "data": {
								"table": {
								  "players": [
									{ "name": "jan" },
									{ "name": "jef" },
									{ "name": "jos" }
								  ]
								}
							  }
							}
							""")

	}

	private fun expectTable(expectedResult: String) {
		val response = request("requests/queryTable.graphql")
		val jsonArray = response.get("$", JSONObject::class.java)

		JSONAssert.assertEquals(expectedResult, jsonArray.toJSONString(), true)
	}

	private fun request(resource: String) = graphQLTestTemplate.postForResource(resource)
}
