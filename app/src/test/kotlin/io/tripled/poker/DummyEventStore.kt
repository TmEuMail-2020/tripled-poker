package io.tripled.poker

import io.tripled.poker.domain.Event
import io.tripled.poker.eventsourcing.EventStore
import java.lang.Exception

class DummyEventStore(private val _events: MutableList<Event> = mutableListOf()) : EventStore {

    private var saveIndex = 0

    override fun save(id: Any, event: Event) {
        this._events.add(event)
    }

    override fun save(id: Any, events: List<Event>) {
        this._events.addAll(events)
    }

    fun init(events: List<Event>) {
        if(saveIndex > 0)
            throw Exception("Should not setup Dummy EventStore twice")
        this._events.addAll(events)
        saveIndex = _events.size
    }


    override fun findById(id: Any): List<Event> {
        return _events
    }

    val events get() = _events.toList()

    val eventsAfterInit get() = _events.subList(saveIndex,_events.size).toList()

    fun contains(element: Event) = this._events.contains(element)

    fun reset(){
        this._events.clear()
        saveIndex = 0
    }
}