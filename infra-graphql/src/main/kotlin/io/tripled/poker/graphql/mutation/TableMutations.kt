package io.tripled.poker.graphql.mutation

import io.tripled.poker.app.api.GameService
import io.tripled.poker.app.api.TableService
import io.tripled.poker.app.api.response.Table
import io.tripled.poker.graphql.AssumeUser
import io.tripled.poker.graphql.Mutation
import org.springframework.stereotype.Component

@Component
class TableMutations(private val tableUseCases: TableService,
                     private val gameUseCases: GameService) : Mutation {

    fun joinTable() = executeTableUseCase() { this.join() }

    fun startGame() = executeTableUseCase() { createGame() }

    fun check() = executeGameUseCase() { check("1") }

    fun fold() = executeGameUseCase() { fold("1") }

    private fun executeTableUseCase(command: TableService.() -> Unit): Table {
        tableUseCases.command()
        return tableUseCases.getTable()
    }

    private fun executeGameUseCase(command: GameService.() -> Unit): Table {
        gameUseCases.command()
        return tableUseCases.getTable()
    }
}