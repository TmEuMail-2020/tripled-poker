package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Table
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.vocabulary.PlayerId

class PokerTable(private val deck: PredeterminedCardTestDeck = PredeterminedCardTestDeck(listOf()),
                 internal val eventStore: DummyEventStore = DummyEventStore(),
                 private val eventPublisher: EventPublisher = DummyEventPublisher(),
                 gameUseCases: GameService = GameUseCases(eventStore, eventPublisher, DummyActiveGames()) { deck },
                 private val tableUseCases: TableService = TableUseCases(eventStore, eventPublisher) { "gameId" })
    : TestPokerGame(deck, eventStore, eventPublisher, gameUseCases, tableUseCases) {

    fun table(asPlayer: PlayerId, table: Table.() -> Unit): PokerTable {
        table.invoke(tableUseCases.getTable(asPlayer))

        return this
    }
}