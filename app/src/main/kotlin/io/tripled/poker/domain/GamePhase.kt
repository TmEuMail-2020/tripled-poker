package io.tripled.poker.domain

enum class GamePhase(val nextPhase: GamePhase) {
    DONE(DONE),
    TURN(DONE),
    FLOP(TURN),
    PRE_FLOP(FLOP);
}
