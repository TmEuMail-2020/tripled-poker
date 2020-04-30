package io.tripled.poker.graphql.query

import io.tripled.poker.app.api.TableService
import io.tripled.poker.app.api.response.Table
import io.tripled.poker.graphql.Query
import org.springframework.stereotype.Component

@Component
class TableResolver(private val tableService: TableService) : Query {
   fun table(): Table {
      return tableService.getTable()
   }
}