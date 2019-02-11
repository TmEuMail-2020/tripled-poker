package io.tripled.poker.graphql.mutation

import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.Table
import io.tripled.poker.graphql.Mutation
import org.springframework.stereotype.Component

@Component
class TableMutations(private val tableService: TableService) : Mutation {

    fun joinTable(name: String): Table {
        tableService.join(name)
        return tableService.getTable()
    }
}