package io.tripled.poker.dsl

import io.tripled.poker.app.api.TableService
import io.tripled.poker.app.api.response.Table

internal class DummyTableUseCases : TableService {
    override fun join() = Unit
    override fun createGame() = Unit
    override fun getTable(): Table = null!!
}