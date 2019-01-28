package io.tripled.poker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
internal class PokerApplication

fun main(vararg args: String) {
    runApplication<PokerApplication>(*args)
}

