package io.tripled.poker.dsl

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.domain.PlayerChecked
import io.tripled.poker.eventsourcing.EventStore
import org.junit.jupiter.api.Test

class DummyEventStoreTest {
    val dummyEventStore: EventStore = DummyEventStore()
    @Test
    internal fun `should return events with right aggregate Id`() {
        dummyEventStore.save("1", listOf(PlayerChecked("Joef")))
        dummyEventStore.save("2", listOf(PlayerChecked("Jef")))

        val eventList = dummyEventStore.findById("2")
        expect(eventList).toBe(listOf(PlayerChecked("Jef")))
    }

    @Test
    internal fun `should append events`() {
        val id = "1"
        dummyEventStore.save(id, listOf(PlayerChecked("Joef")))
        dummyEventStore.save(id, listOf(PlayerChecked("Jef")))

        val eventList = dummyEventStore.findById(id)
        expect(eventList).toBe(listOf(PlayerChecked("Joef"), PlayerChecked("Jef")))
    }
}