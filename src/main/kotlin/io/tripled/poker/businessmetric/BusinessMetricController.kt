package io.tripled.poker.businessmetric

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class BusinessMetricController(registry: MeterRegistry) {
    val counter = registry.counter("business.metric")

    @GetMapping("/api/businessMetric")
    @Timed
    fun incrementCounter() {
        counter.increment()
    }
}
