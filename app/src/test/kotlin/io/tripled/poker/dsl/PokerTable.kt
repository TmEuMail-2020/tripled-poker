package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Table
import io.tripled.poker.domain.EventPublisher
import io.tripled.poker.domain.PlayerId
import io.tripled.poker.domain.PredeterminedCardDeck

class PokerTable(private val deck: PredeterminedCardDeck = PredeterminedCardDeck(listOf()),
                 internal val eventStore: DummyEventStore = DummyEventStore(),
                 private val eventPublisher: EventPublisher = DummyEventPublisher(),
                 private val gameUseCases: GameService = GameUseCases(eventStore, { deck }, eventPublisher, DummyActiveGames()),
                 private val tableUseCases: TableService = TableUseCases(eventStore, gameUseCases, eventPublisher) { "gameId" })
    : TestPokerGame(deck, eventStore, eventPublisher, gameUseCases, tableUseCases) {

    fun table(asPlayer: PlayerId, table: Table.() -> Unit): PokerTable {
        table.invoke(tableUseCases.getTable(asPlayer))

        return this
    }
}