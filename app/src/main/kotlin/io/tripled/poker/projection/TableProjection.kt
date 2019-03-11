package io.tripled.poker.projection

import io.tripled.poker.api.response.Card
import io.tripled.poker.api.response.Player
import io.tripled.poker.api.response.Table
import io.tripled.poker.domain.CardsAreDealt
import io.tripled.poker.domain.PlayerJoinedTable
import io.tripled.poker.domain.PlayerWonRound

class TableProjection(private val playerName: String, events: List<Any>) {

    val table: Table

    init {
        table = Table(players(events), winner(events))
    }

    private fun winner(events: List<Any>): Player? {
        val playerWonRound = events.lastOrNull { it is PlayerWonRound } as PlayerWonRound?
        return if (playerWonRound != null)
            playerWithCards(events, playerWonRound.name)
        else
            null
    }

    private fun players(events: List<Any>): List<Player> {
        return events
                .filter { it is PlayerJoinedTable }
                .map { event ->
                    val name = (event as PlayerJoinedTable).name
                    obfuscate(playerWithCards(events, name))
                }
    }

    private fun obfuscate(playerWithCards: Player) =
            Player(playerWithCards.name, playerWithCards.cards.map {
                if (playerWithCards.name == playerName)
                    it
                else
                    Card.HIDDEN
            })

    private fun playerWithCards(events: List<Any>, player: String): Player {
        val lastDealtCards = events.lastOrNull { it is CardsAreDealt } as CardsAreDealt?
        return if (lastDealtCards != null) {
            val card = lastDealtCards.cards[player]
            if (card != null) {
                Player(player, listOf(Card(card.suit, card.value)))
            } else {
                Player(player)
            }
        } else {
            Player(player)
        }
    }

}
