package io.tripled.poker.dsl

import io.tripled.poker.domain.*
import io.tripled.poker.domain.cards.Card
import io.tripled.poker.domain.cards.Hand
import io.tripled.poker.domain.game.*
import io.tripled.poker.eventsourcing.EventStore
import io.tripled.poker.vocabulary.GameId
import io.tripled.poker.vocabulary.PlayerId

class DummyGameRepository(private val eventStore: EventStore) : GameRepository {
    override fun save(gameId: GameId, events: List<Event>) = eventStore.append(gameId, events)

    override fun findGameById(gameId: GameId): Game = Game(GameReducer().of(eventStore.findById(gameId)))

    internal class GameReducer {
        fun of(events: List<Event>) = GameState(players(events),
                hands(events),
                remainingCards(events),
                countChecks(events),
                phase(events),
                lastPlayer(events))

        private fun countChecks(events: List<Event>): Int =
                events.filterEvents<PlayerChecked>().size

        private fun lastPlayer(events: List<Event>): PlayerId? {
            return events.filter { e -> e is PlayerFolded || e is PlayerChecked }.lastOrNull { e ->
                return when (e) {
                    is PlayerFolded -> e.name
                    is PlayerChecked -> e.name
                    else -> ""
                }
            }?.toString() ?: players(events).lastOrNull()

        }

        private fun players(events: List<Event>): List<PlayerId> {
            val allPlayers = hands(events).keys.toList()
            val foldedPlayers = events.filterEvents<PlayerFolded>().map { t -> t.name }

            return allPlayers - foldedPlayers
        }

        private fun hands(events: List<Event>): Map<PlayerId, Hand> {
            val lastEventOrNull = events
                    .lastEventOrNull<HandsAreDealt>()
            return lastEventOrNull?.hands ?: mapOf()
        }

        private fun remainingCards(events: List<Event>): List<Card> {
            val lastEventOrNull = events
                    .lastEventOrNull<GameStarted>() ?: return listOf()

            val cards = lastEventOrNull.cardsInDeck.toMutableList()
            return events.fold(cards) { _, event ->
                when (event) {
                    is HandsAreDealt -> cards.removeAll(
                            event.hands.flatMap { it.value.cards() }
                    )
                    is FlopIsTurned -> cards.removeAll(
                            listOf(event.card1, event.card2, event.card3)
                    )
                    is TurnIsTurned -> cards.remove(event.card)
                    is RiverIsTurned -> cards.remove(event.card)
                }
                cards
            }
        }

        private fun phase(events: List<Event>): GamePhase =
                events.fold(GamePhase.PRE_FLOP, { acc, event ->
                    when (event) {
                        is FlopIsTurned -> GamePhase.FLOP
                        is TurnIsTurned -> GamePhase.TURN
                        is RiverIsTurned -> GamePhase.RIVER
                        is PlayerWonGame -> GamePhase.DONE
                        else -> acc
                    }
                })
    }

}