package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Table
import io.tripled.poker.domain.PlayerId
import io.tripled.poker.eventpublishing.EventPublisher

class PokerTable(private val deck: PredeterminedCardTestDeck = PredeterminedCardTestDeck(listOf()),
                 internal val eventStore: DummyEventStore = DummyEventStore(),
                 private val eventPublisher: EventPublisher = DummyEventPublisher(),
                 private val gameUseCases: GameService = GameUseCases(eventStore, eventPublisher, DummyActiveGames(), { deck }),
                 private val tableUseCases: TableService = TableUseCases(eventStore, gameUseCases, eventPublisher) { "gameId" })
    : TestPokerGame(deck, eventStore, eventPublisher, gameUseCases, tableUseCases) {

    fun table(asPlayer: PlayerId, table: Table.() -> Unit): PokerTable {
        table.invoke(tableUseCases.getTable(asPlayer))

        return this
    }
}