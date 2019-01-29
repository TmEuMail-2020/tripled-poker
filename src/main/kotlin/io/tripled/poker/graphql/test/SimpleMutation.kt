package io.tripled.poker.graphql.test

import com.expedia.graphql.annotations.GraphQLDescription
import io.tripled.poker.graphql.Mutation
import org.springframework.stereotype.Component

@Component
class SimpleMutation: Mutation {

    private val data: MutableList<String> = mutableListOf()

    @GraphQLDescription("add value to a list and return resulting list")
    fun addToList(entry: String): MutableList<String> {
        data.add(entry)
        return data
    }
}