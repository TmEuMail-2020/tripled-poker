package io.tripled.poker.domain

import io.tripled.poker.vocabulary.Suit.*
import io.tripled.poker.vocabulary.Value.*

class DeckMother {
    fun fullDeck():List<Card>{
        return deckOfHearts() + deckOfClubs() + deckOfDiamonds() + deckOfSpades()
    }

    fun deckOfHearts(): List<Card> {
        return listOf(
                TEN of HEARTS,
                ACE of HEARTS,
                KING of HEARTS,
                QUEEN of HEARTS,
                NINE of HEARTS,
                EIGHT of HEARTS,
                SEVEN of HEARTS,
                SIX of HEARTS,
                FIVE of HEARTS,
                FOUR of HEARTS,
                THREE of HEARTS,
                TWO of HEARTS
        )
    }

    fun deckOfSpades(): List<Card> {
        return listOf(
                TEN of SPADES,
                ACE of SPADES,
                KING of SPADES,
                QUEEN of SPADES,
                NINE of SPADES,
                EIGHT of SPADES,
                SEVEN of SPADES,
                SIX of SPADES,
                FIVE of SPADES
        )
    }

    fun deckOfDiamonds(): List<Card> {
        return listOf(
                TEN of DIAMONDS,
                ACE of DIAMONDS,
                KING of DIAMONDS,
                QUEEN of DIAMONDS,
                NINE of DIAMONDS,
                EIGHT of DIAMONDS,
                SEVEN of DIAMONDS,
                SIX of DIAMONDS,
                FIVE of DIAMONDS
        )
    }
    fun deckOfClubs(): List<Card> {
        return listOf(
                TEN of CLUBS,
                ACE of CLUBS,
                KING of CLUBS,
                QUEEN of CLUBS,
                NINE of CLUBS,
                EIGHT of CLUBS,
                SEVEN of CLUBS,
                SIX of CLUBS,
                FIVE of CLUBS
        )
    }
}
