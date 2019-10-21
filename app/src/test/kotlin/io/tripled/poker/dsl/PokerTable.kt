package io.tripled.poker.dsl

import io.tripled.poker.api.GameService
import io.tripled.poker.api.GameUseCases
import io.tripled.poker.api.TableService
import io.tripled.poker.api.TableUseCases
import io.tripled.poker.api.response.Table
import io.tripled.poker.eventpublishing.EventPublisher
import io.tripled.poker.projection.ActiveGames
import io.tripled.poker.vocabulary.PlayerId

class PokerTable(private val deck: PredeterminedCardTestDeck = PredeterminedCardTestDeck(listOf()),
                 internal val eventStore: DummyEventStore = DummyEventStore(),
                 private val eventPublisher: EventPublisher = DummyEventPublisher(),
                 private val assumeUser: AssumeUser = AssumeUser(),
                 private val activeGames: ActiveGames = DummyActiveGames(),
                 private val gameUseCases: GameService = GameUseCases(eventStore, eventPublisher, activeGames, assumeUser) { deck },
                 private val tableUseCases: TableService = TableUseCases(eventStore, eventPublisher, assumeUser) { "gameId" })
    : TestPokerGame(deck, eventStore, eventPublisher, assumeUser, activeGames, gameUseCases, tableUseCases) {

    fun table(asPlayer: PlayerId, table: Table.() -> Unit): PokerTable {
        assumeUser.assumedPlayerId = asPlayer
        table.invoke(tableUseCases.getTable())

        return this
    }
}