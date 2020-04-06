package io.tripled.poker.graphql.query

import io.tripled.poker.app.api.TableService
import io.tripled.poker.app.api.response.Table
import io.tripled.poker.domain.Users
import io.tripled.poker.graphql.AssumeUser
import io.tripled.poker.graphql.Query
import org.springframework.stereotype.Component

@Component
class TableResolver(private val tableService: TableService) : Query {
   fun table(name: String): Table {
      return tableService.getTable()
   }
}