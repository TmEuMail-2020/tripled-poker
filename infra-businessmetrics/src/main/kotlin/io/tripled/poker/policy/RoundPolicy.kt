package io.tripled.poker.policy

import io.tripled.poker.domain.PlayerChecked
import io.tripled.poker.domain.Table
import io.tripled.poker.domain.TableState
import io.tripled.poker.eventsourcing.EventStore
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class RoundPolicy(private val eventStore: EventStore) {

    // TODO: move me to other module
    @EventListener
    fun playerChecked(playerChecked: PlayerChecked) {
        // trigger a usecase on the table

        val table = Table(TableState.of(eventStore.findById(1)))
        table.tryToFlop()


    }
}