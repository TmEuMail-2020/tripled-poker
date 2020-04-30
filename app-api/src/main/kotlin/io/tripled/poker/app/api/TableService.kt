package io.tripled.poker.app.api

import io.tripled.poker.app.api.response.Table

interface TableService {
    fun join()
    fun createGame()
    fun getTable(): Table
}

