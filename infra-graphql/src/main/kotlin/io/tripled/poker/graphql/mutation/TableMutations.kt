package io.tripled.poker.graphql.mutation

import io.tripled.poker.api.GameService
import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.Table
import io.tripled.poker.graphql.Mutation
import org.springframework.stereotype.Component

@Component
class TableMutations(private val tableUseCases: TableService, private val gameUseCases: GameService) : Mutation {

    fun joinTable(name: String) = executeTableUseCase(name) { join(name) }

    fun startGame(name: String) = executeTableUseCase(name) { createGame() }

    fun check(name: String) = executeGameUseCase(name) { check(name) }

    private fun executeTableUseCase(name: String, tableUseCase: TableService.() -> Unit): Table {
        tableUseCases.tableUseCase()
        return tableUseCases.getTable(name)
    }

    private fun executeGameUseCase(name: String, gameUseCase: GameService.() -> Unit): Table {
        gameUseCases.gameUseCase()
        return tableUseCases.getTable(name)
    }
}