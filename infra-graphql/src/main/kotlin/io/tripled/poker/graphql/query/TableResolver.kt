package io.tripled.poker.graphql.query

import io.tripled.poker.api.TableService
import io.tripled.poker.graphql.Query
import org.springframework.stereotype.Component

@Component
class TableResolver(private val tableService: TableService) : Query {
   fun table() = tableService.getTable()
}