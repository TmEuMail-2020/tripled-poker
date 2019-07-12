package poker

import deck.backOfCardImage
import deck.cardImage
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyPressFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*

enum class Suit { DIAMOND, SPADES, HEART, CLUB }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
data class Card(val value: String, val suit: String){
    val typedValue = Value.valueOf(value)
    val typedSuit = Suit.valueOf(suit)
}
data class Cards(val numberOfCards: Int, val visibleCards: Array<Card> = emptyArray())
data class Player(val name: String, val cards: Cards)
data class Table(val players: Array<Player> = emptyArray(), val winner: Player? = null)

fun RBuilder.pokerTable() = child(PokerTableRepresentation::class) {}

class PokerTableRepresentation : RComponent<RProps, RState>() {
    private val pokerApi = PokerApi()
    private var table: Table = Table()
    private var playerName = ""

    override fun componentDidMount() {
        pokerApi.getTable(::updateTable)
    }

    private fun updateTable(returnedTable: Table) {
        setState {
            table = returnedTable
        }
    }

    override fun RBuilder.render() {
        h1 { +"Poker table" }

        joinGame { newPlayerName ->
            pokerApi.joinTable(newPlayerName, ::updateTable)
            setState {
                playerName = newPlayerName
            }
        }
        if (playerName.isNotEmpty()){
            h1 { + "Playing as player $playerName" }
        }
        input(type = InputType.button) {
            attrs {
                value = "Check"
                onClickFunction = { event: Event ->
                    pokerApi.check(::updateTable)
                }
            }
        }
        input(type = InputType.button) {
            attrs {
                value = "Play round"
                onClickFunction = { event: Event ->
                    pokerApi.playRound(::updateTable)
                }
            }
        }
        input(type = InputType.button) {
            attrs {
                value = "Refresh table"
                onClickFunction = { event: Event ->
                    pokerApi.getTable(::updateTable)
                }
            }
        }
        h1 { + "Winner: ${table.winner?.name}" }
        if (table.winner != null){
            cards(table.winner?.cards!!)
        }
        playerList(table)
    }

}

interface PlayerListProps : RProps {
    var table: Table
}

fun RBuilder.playerList(table: Table) = child(PlayerList::class) { attrs.table = table }

class PlayerList : RComponent<PlayerListProps, RState>() {
    override fun RBuilder.render() {
        ul(classes = "players") {
            props.table.players.map { player ->
                li(classes = "player") {
                    p { + player.name }
                    cards(player.cards)
                }
            }
        }
    }
}

fun RBuilder.joinGame(onJoinGame: (String) -> Any) = child(JoinGame::class) { attrs.onJoinGame = onJoinGame }

interface JoinGameProps : RProps {
    var onJoinGame: (String) -> Any
}

class JoinGame : RComponent<JoinGameProps, RState>() {
    private var playerName: String = ""

    override fun RBuilder.render() {
        if (playerName.isEmpty()){
            p { +"Join the game" }
            input(type = InputType.text) {
                attrs {
                    placeholder = "enter player name"
                    onChangeFunction = { event: Event ->
                        playerName = (event.target as HTMLInputElement).value
                    }
                    onKeyPressFunction = { syntheticEvent: Event ->
                        val event = syntheticEvent.asDynamic().nativeEvent
                        if (event is KeyboardEvent) {
                            if (event.key == "Enter") {
                                val target = event.target as HTMLInputElement
                                props.onJoinGame(playerName)
                                setState {
                                    target.value = ""
                                }
                            }
                        }
                    }
                }
            }
            input(type = InputType.button) {
                attrs {
                    value = "Join game"
                    onClickFunction = { event: Event ->
                        props.onJoinGame(playerName)
                    }
                }
            }
        }
    }
}

fun RBuilder.cards(cards: Cards) = child(CardComponent::class) { attrs.cards = cards }

interface CardProps : RProps {
    var cards: Cards
}

class CardComponent : RComponent<CardProps, RState>() {
    override fun RBuilder.render() {
        val visibleCards = props.cards.visibleCards
        if (visibleCards.isEmpty()) {
            (1..props.cards.numberOfCards).map {
                img(src = backOfCardImage()){ }
            }
        } else {
            visibleCards.map {
                card -> img(src = cardImage(card)) { }
            }
        }
    }
}