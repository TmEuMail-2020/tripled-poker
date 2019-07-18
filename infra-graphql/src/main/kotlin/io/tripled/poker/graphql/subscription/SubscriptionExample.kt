package io.tripled.poker.graphql.subscription

import com.expedia.graphql.annotations.GraphQLDescription
import io.tripled.poker.graphql.Subscription
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

@Component
class SubscriptionExample : Subscription {
    @GraphQLDescription("Returns a random number every second")
    fun counter(): Flux<Int> = Flux.interval(Duration.ofSeconds(1)).map { Random().nextInt() }
}