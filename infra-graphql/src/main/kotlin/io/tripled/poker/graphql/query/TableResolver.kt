package io.tripled.poker.graphql.query

import io.tripled.poker.graphql.Query
import org.springframework.stereotype.Component

data class Player(val name: String)
data class Table(val players: List<Player>)

@Component
class TableResolver: Query {
   fun table()= Table(listOf())
}