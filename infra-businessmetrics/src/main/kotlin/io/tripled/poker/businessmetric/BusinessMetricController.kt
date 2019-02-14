package io.tripled.poker.businessmetric

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import io.tripled.poker.domain.PlayerJoinedTable
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class BusinessMetricController(registry: MeterRegistry) {
    val counter = registry.counter("business.metric")

    val nbOfPlayers = registry.counter("business.nbOfPlayers")

    @GetMapping("/api/businessMetric")
    @Timed
    fun incrementCounter() {
        counter.increment()
    }

    @EventListener
    fun playerJoined(playerJoined: PlayerJoinedTable) {
        nbOfPlayers.increment()
    }
}
