package io.tripled.poker

import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import io.tripled.poker.api.response.*
import io.tripled.poker.api.response.Card
import io.tripled.poker.api.response.Suit.*
import io.tripled.poker.api.response.Value.*
import io.tripled.poker.domain.*
import io.tripled.poker.domain.mapToCard
import io.tripled.poker.dsl.pokerTableTest
import io.tripled.poker.dsl.pokerTableTestNoEventAssert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetTableUseCasesTest {
    private val Jef = "Jef"
    private val Joe = "Joe"

    @Test
    internal fun `a new table has no players`() = pokerTableTest {
        table(Jef) {
            assertEquals(0, players.size)
        }
    }

    @Test
    internal fun `a table with players`() = pokerTableTest {
        given {
            withPlayers(Joe, Jef)
        }

        table(Jef) {
            expect(players).toBe(listOf(
                    Player(Joe),
                    Player(Jef)
            ))
        }
    }

    @Test
    internal fun `a table with players and I can only see my own cards`() = pokerTableTest {
        given {
            withPlayers(Joe, Jef)
            startGame(DeckMother().deckOfHearts())
            preflop(Joe to suitedConnectors,
                    Jef to suitedAceKing) {
            }
        }

        table(Joe) {
            expect(players).toBe(listOf(
                    Player(Joe, VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                    Player(Jef, HiddenCards(2))
            ))
        }
    }

    @Test
    internal fun `a table with a winner`() = pokerTableTest {
        given {
            withPlayers(Joe, Jef)
            startGame(DeckMother().deckOfHearts())
            preflop(Joe to suitedConnectors,
                    Jef to suitedAceKing) {
            }
            expectWinner(Jef)
        }

        table(Joe) {
            expect(winner).toBe(
                    Player(Jef, VisibleCards(suitedAceKing.cards().map { it.mapToCard() }))
            )
        }
    }

    @Test
    @Disabled
    internal fun `new deck is created between games`() = pokerTableTestNoEventAssert {
        withPlayers("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        repeat((1..10).count()) {
            withCards(ShuffledDeck().cards)
            preflop {}
        }
    }

    @Test
    internal fun `player joins after cards are dealt`() = pokerTableTest {
        given {
            withPlayers(Joe)
            startGame(DeckMother().deckOfHearts())
            preflop(Joe to suitedConnectors) {}
            withPlayers(Jef)
        }

        table(Joe) {
            expect(players).toBe(listOf(
                    Player(Joe, VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                    Player(Jef)
            ))
        }
    }

    @Test
    internal fun `flop turn and river cards are visible on the table`() = pokerTableTest {
        given {
            withPlayers(Joe, Jef)
            startGame(DeckMother().deckOfHearts())
            preflop(Joe to suitedConnectors,
                    Jef to suitedAceKing) {
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
        }

        table(Joe) {
            expect(players).toBe(listOf(
                    Player(Joe, VisibleCards(suitedConnectors.cards().map { it.mapToCard() })),
                    Player(Jef, HiddenCards(2))
            ))

            expect(flop).toBe(VisibleCards(listOf(
                    Card(HEARTS, NINE),
                    Card(HEARTS, EIGHT),
                    Card(HEARTS, SEVEN))))
            expect(turn).toBe(VisibleCards(listOf(Card(HEARTS, SIX))))
            expect(river).toBe(VisibleCards(listOf(Card(HEARTS, FIVE))))
        }
    }
}