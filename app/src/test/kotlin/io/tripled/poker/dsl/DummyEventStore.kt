package io.tripled.poker.dsl

import io.tripled.poker.domain.*
import io.tripled.poker.eventsourcing.EventStore

class DummyEventStore(var given: List<Event> = listOf()) : EventStore {
    private val newlyDispatchedEvents = given.toMutableList()

    fun given(pokerBuilder: EventBuilder.() -> Unit) {
        val builder = EventBuilder()
        builder.pokerBuilder()
        given = builder.events
    }

    override fun save(id: Any, events: List<Event>) {
        newlyDispatchedEvents += events
    }

    override fun findById(id: Any): List<Event> {
        return given + newlyDispatchedEvents
    }

    val newEvents get() = newlyDispatchedEvents.toList()

    fun contains(element: Event) = this.newlyDispatchedEvents.contains(element)

    class EventBuilder {
        val events = mutableListOf<Event>()

        private fun playersJoin(vararg players: PlayerId) {
            events += players.map { PlayerJoinedTable(it) }
        }

        fun startGame(gameId: GameId, cardsInDeck: List<Card>, vararg playerHands: Pair<PlayerId, Hand>) {
            val playerIds = playerHands.map { p -> p.first }.toTypedArray()
            playersJoin(*playerIds)
            events += GameStarted(gameId, playerIds.toList(), cardsInDeck)
            events += HandsAreDealt(mapOf(*playerHands))
        }
    }
}