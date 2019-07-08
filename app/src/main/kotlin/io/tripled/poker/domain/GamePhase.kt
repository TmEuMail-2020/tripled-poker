package io.tripled.poker.domain

enum class GamePhase(val nextPhase: GamePhase) {
    DONE(DONE),
    RIVER(DONE),
    TURN(RIVER),
    FLOP(TURN),
    PRE_FLOP(FLOP);
}
