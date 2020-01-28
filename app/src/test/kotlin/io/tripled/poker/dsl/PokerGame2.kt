package io.tripled.poker.dsl

import io.tripled.poker.domain.game.GameState
import io.tripled.poker.domain.table.TableState
import io.tripled.poker.vocabulary.PlayerId

fun pokerGameTest2(test: PokerGameTest.() -> Unit) = PokerGameTest()
        .apply(test)
        .assert()

class PokerGameTest {

    private lateinit var initialPokerGameState: PokerGame
    private lateinit var assertions: (pg: PokerGame) -> Unit

    fun given(pokerGameBuilder: PokerGameBuilder.() -> Unit) {
        val pkgb = PokerGameBuilder()
        pkgb.pokerGameBuilder()
        initialPokerGameState = pkgb.build()
    }

    fun then(assertions: (pg: PokerGame) -> Unit){
        this.assertions = assertions
    }

    fun assert() {
        assertions.invoke(initialPokerGameState)
    }

}

@DslMarker
annotation class TableDsl

@TableDsl
class TableBuilder {

    private val players = mutableListOf<PlayerId>()

    fun build(): TableState {
        return TableState(players)
    }

    operator fun PlayerId.unaryPlus() {
        players += this
    }

    operator fun PlayerId.unaryMinus() {
        players += this
    }

}

@DslMarker
annotation class PokerGameDsl

@PokerGameDsl
class PokerGameBuilder {

    private val tableBuilder: TableBuilder = TableBuilder()
    val game: GameState? = null

    fun table(tableBuilderActions: TableBuilder.() -> Unit) {
        tableBuilder.tableBuilderActions()
    }

    fun build(): PokerGame {
        return PokerGame(tableBuilder.build(), game!!)
    }


}

data class PokerGame(val tableState: TableState, val gameState: GameState)