package io.tripled.poker.domain

enum class GamePhase(val nextPhase: GamePhase) {
    DONE(DONE),
    FLOP(DONE),
    PRE_FLOP(FLOP);
}
