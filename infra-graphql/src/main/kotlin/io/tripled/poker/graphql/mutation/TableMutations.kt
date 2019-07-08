package io.tripled.poker.graphql.mutation

import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.Table
import io.tripled.poker.graphql.Mutation
import org.springframework.stereotype.Component

@Component
class TableMutations(private val tableService: TableService) : Mutation {

    fun joinTable(name: String) = executeUseCase(name) { join(name) }

    fun startRound(name: String) = executeUseCase(name) { startGame() }

    fun check(name: String) = executeUseCase(name) { check(name) }

    private fun executeUseCase(name: String, usecase: TableService.() -> Unit): Table {
        tableService.usecase()
        return tableService.getTable(name)
    }

}