package io.tripled.poker.graphql.query

import io.tripled.poker.app.api.TableService
import io.tripled.poker.app.api.response.Table
import io.tripled.poker.graphql.AssumeUser
import io.tripled.poker.graphql.Query
import org.springframework.stereotype.Component

@Component
class TableResolver(private val tableService: TableService,
                    private val assumeUser: AssumeUser) : Query {
   fun table(name: String): Table {
      assumeUser.assumedPlayerId = name
      return tableService.getTable()
   }
}