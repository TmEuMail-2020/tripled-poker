package io.tripled.poker.app.api

interface TableService {
    fun join()
    fun createGame()
    fun getTable(): io.tripled.poker.app.api.response.Table
}

