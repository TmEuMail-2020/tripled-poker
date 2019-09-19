package io.tripled.poker.domain

import io.tripled.poker.vocabulary.PlayerId

data class User(val playerId: PlayerId)

interface Users {
    val currentUser: User
}