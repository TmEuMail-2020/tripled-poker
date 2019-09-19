package io.tripled.poker.graphql.mutation

import io.tripled.poker.graphql.AssumeUser
import io.tripled.poker.api.GameService
import io.tripled.poker.api.TableService
import io.tripled.poker.api.response.Table
import io.tripled.poker.graphql.Mutation
import org.springframework.stereotype.Component

@Component
class TableMutations(private val tableUseCases: TableService,
                     private val gameUseCases: GameService,
                     private val assumeUser: AssumeUser) : Mutation {

    fun joinTable(name: String) = executeTableUseCase(name) { this.join() }

    fun startGame(name: String) = executeTableUseCase(name) { createGame() }

    fun check(name: String) = executeGameUseCase(name) { check("1") }

    private fun executeTableUseCase(name: String, command: TableService.() -> Unit): Table {
        assumeUser.assumedPlayerId = name
        tableUseCases.command()
        return tableUseCases.getTable()
    }

    private fun executeGameUseCase(name: String, command: GameService.() -> Unit): Table {
        assumeUser.assumedPlayerId = name
        gameUseCases.command()
        return tableUseCases.getTable()
    }
}