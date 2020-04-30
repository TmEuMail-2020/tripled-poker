package io.tripled.poker.graphql

import io.tripled.poker.domain.User
import io.tripled.poker.domain.Users
import io.tripled.poker.vocabulary.PlayerId

class AssumeUser : Users {
    private val currentAssumption: ThreadLocal<PlayerId> = ThreadLocal()
    var assumedPlayerId: PlayerId = "Anonymous"
        set(value) {
            currentAssumption.set(value)
        }

    override val currentUser: User
        get() = User(currentAssumption.get())
}