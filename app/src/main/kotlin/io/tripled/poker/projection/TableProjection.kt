package io.tripled.poker.projection

import io.tripled.poker.api.response.*
import io.tripled.poker.domain.CardsAreDealt
import io.tripled.poker.domain.PlayerJoinedTable
import io.tripled.poker.domain.PlayerWonRound
import sun.audio.AudioPlayer.player
import java.util.function.Function

class TableProjection(private val playerName: String, events: List<Any>) {

    val table: Table

    init {
        table = Table(players(events), winner(events))
    }

    private fun winner(events: List<Any>): Player? {
        val playerWonRound = events.lastOrNull { it is PlayerWonRound } as PlayerWonRound?
        return if (playerWonRound != null)
            playerWithCards(events, playerWonRound.name){cards -> VisibleCards(cards)}
        else
            null
    }

    private fun players(events: List<Any>): List<Player> {
        return events
                .filter { it is PlayerJoinedTable }
                .map { event ->
                    val name = (event as PlayerJoinedTable).name
                    val playerWithCards = playerWithCards(events, name, obfuscateCards(name))
                    Player(playerWithCards.name, playerWithCards.cards)
                }
    }

    private fun obfuscateCards(name: String): (List<Card>) -> Cards {
        return { cards ->
            if (name == playerName) {
                VisibleCards(cards)
            } else {
                HiddenCards(cards.size)
            }
        }
    }

    private fun playerWithCards(events: List<Any>, player: String, cardMapper: (List<Card>) -> Cards): Player {
        val lastDealtCards = events.lastOrNull { it is CardsAreDealt } as CardsAreDealt?
        return if (lastDealtCards != null) {
            val card = lastDealtCards.cards[player]
            if (card != null) {
                Player(player, cardMapper.invoke(listOf(Card(card.suit, card.value))))
            } else {
                Player(player)
            }
        } else {
            Player(player)
        }
    }

}
