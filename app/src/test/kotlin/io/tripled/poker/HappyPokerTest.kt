package io.tripled.poker

import io.tripled.poker.domain.DeckMother
import io.tripled.poker.domain.of
import io.tripled.poker.dsl.pokerGameTest
import io.tripled.poker.vocabulary.Suit.*
import io.tripled.poker.vocabulary.Value.*
import org.junit.jupiter.api.Test

class HappyPokerTest {
    private val Joe = "Joe"
    private val Jef = "Jef"
    private val kris = "kris"
    private val gert = "gert"
    private val Clem = "Clem"

    @Test
    internal fun `real world test`() = pokerGameTest {
        withPlayers(kris, gert, Clem)
        startGame(listOf(FOUR of SPADES,NINE of DIAMONDS,TWO of CLUBS,KING of CLUBS,JACK of DIAMONDS,ACE of DIAMONDS,QUEEN of SPADES,EIGHT of HEARTS,ACE of SPADES,TEN of HEARTS,TEN of CLUBS,SEVEN of SPADES,JACK of CLUBS,SIX of HEARTS,SEVEN of HEARTS,KING of HEARTS,FIVE of HEARTS,NINE of HEARTS,TWO of SPADES,SIX of CLUBS,QUEEN of CLUBS,FOUR of DIAMONDS,FIVE of SPADES,TWO of HEARTS,FIVE of CLUBS,THREE of CLUBS,ACE of CLUBS,SEVEN of CLUBS,KING of SPADES,THREE of DIAMONDS,SIX of SPADES,KING of DIAMONDS,JACK of SPADES,EIGHT of DIAMONDS,QUEEN of HEARTS,NINE of CLUBS,THREE of HEARTS,TEN of SPADES,JACK of HEARTS,TWO of DIAMONDS,FOUR of CLUBS,NINE of SPADES,QUEEN of DIAMONDS,TEN of DIAMONDS,THREE of SPADES,FOUR of HEARTS))
        preflop(
                kris to ((FIVE of DIAMONDS) and (EIGHT of CLUBS)),
                gert to ((ACE of HEARTS) and (SIX of DIAMONDS)),
                Clem to ((EIGHT of SPADES) and (SEVEN of DIAMONDS))
        ) {

        }
        flop(FOUR of SPADES,
                NINE of DIAMONDS,
                TWO of CLUBS
        ) {
            kris.checks()
            gert.checks()
            Clem.checks()
        }
        turn(KING of CLUBS) {
            kris.checks()
            gert.checks()
            Clem.checks()
        }
        river(JACK of DIAMONDS) {
            kris.checks()
            gert.checks()
            Clem.checks()
        }
        expectWinner(gert)
    }
    @Test
    internal fun `test all usecases and events to play game with two players`() = pokerGameTest {
        withPlayers(Joe, Jef)
        startGame(DeckMother().deckOfHearts())
        preflop(
                Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
        ) {
            Joe.checks()
            Jef.checks()
        }
        flop(NINE of HEARTS,
                EIGHT of HEARTS,
                SEVEN of HEARTS
        ) {
            Joe.checks()
            Jef.checks()
        }
        turn(SIX of HEARTS) {
            Joe.checks()
            Jef.checks()
        }
        river(FIVE of HEARTS) {
            Joe.checks()
            Jef.checks()
        }
        expectWinner(Jef)
    }

    @Test
    internal fun `test all usecases and events to play game with two players 2`() = pokerGameTest {
        withPlayers(Joe, Jef)
        startGame(DeckMother().deckOfHearts())
        preflop(
                Joe to ((TEN of HEARTS) and (ACE of HEARTS)),
                Jef to ((KING of HEARTS) and (QUEEN of HEARTS))
        ) {
            Joe.checks()
            Jef.checks()
        }
        flop(NINE of HEARTS,
                EIGHT of HEARTS,
                SEVEN of HEARTS
        ) {
            Joe.checks()
            Jef.checks()
        }
        turn(SIX of HEARTS) {
            Joe.checks()
            Jef.checks()
        }
        river(FIVE of HEARTS) {
            Joe.checks()
            Jef.folds()
        }
        expectWinner(Joe)
    }
}
