package io.tripled.poker.graphql.test

import com.expedia.graphql.annotations.GraphQLDescription
import io.tripled.poker.graphql.Mutation
import org.springframework.stereotype.Component

@Component
class SimpleMutation: Mutation {

    data class Test(val a: String, val b: Int)

    private val data: MutableList<Test> = mutableListOf()

    @GraphQLDescription("add value to a list and return resulting list")
    fun addToList(entry: String): List<Test> {
        data.add(Test(entry, data.size))
        return data
    }
}