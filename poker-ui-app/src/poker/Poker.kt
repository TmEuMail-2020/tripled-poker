package poker

import deck.*
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyPressFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*

enum class Suit { DIAMONDS, SPADES, HEARTS, CLUBS }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
data class Card(val value: String, val suit: String){
    val typedValue = Value.valueOf(value)
    val typedSuit = Suit.valueOf(suit)
}
data class Cards(val numberOfCards: Int, val visibleCards: Array<Card> = emptyArray())
data class Player(val name: String, val cards: Cards)
data class Table(val players: Array<Player> = emptyArray(),
                 val flop: Cards = Cards(0),
                 val turn: Cards = Cards(0),
                 val river: Cards = Cards(0),
                 val winner: Player? = null,
                 val dsl: String = "")

fun RBuilder.pokerTable() = child(PokerTableRepresentation::class) {}

class PokerTableRepresentation : RComponent<RProps, RState>() {
    private val pokerApi = PokerApi()
    private val eventApi = EventStreamApi()
    private var table: Table = Table()
    private var playerName = ""
    private var tableEvents: Array<dynamic> = emptyArray()
    private var selectedGameId: String = ""
    private var gameEvents: Array<dynamic> = emptyArray()
    private var gameDsl: String = ""

    override fun componentDidMount() {
        pokerApi.getTable(::updateTable)
    }

    private fun updateTable(returnedTable: Table) {
        eventApi.events({ data -> setState { tableEvents = data }}, "1")
        updateGameStream()
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
                value = "Fold"
                onClickFunction = { event: Event ->
                    pokerApi.fold(::updateTable)
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

        table {
            thead {
                tr {
                    th { + "Flop" }
                    th { + "Turn" }
                    th { + "River" }
                }
            }
            tbody {
                tr {
                    td { cards(table.flop, card1BRed) }
                    td { cards(table.turn, card1BGreen) }
                    td { cards(table.river, card1BBlue) }
                }
            }
        }

        playerList(table)

        table(classes = "table") {
            thead {
                tr {
                    th {
                        + "Table stream"
                    }
                    th {
                        + "Game stream"
                    }
                    th {
                        + "DSL"
                    }
                }
            }
            tbody {
                tr {
                    td {
                        eventStream(tableEvents)
                    }
                    td {
                        a {
                            attrs {
                                id = "gamestream"
                            }
                        }
                        eventStream(gameEvents)
                    }
                    td {
                        pre {
                            + gameDsl
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.eventStream(events: Array<dynamic>) {
        ol {
            events.map { e ->
                li(classes = "tilesWrap") {
                    if (JSON.stringify(e.payload).contains("GameCreated")) {
                        a {
                            attrs {
                                onClickFunction = {
                                    clickEvent ->
                                    selectedGameId = e.payload.gameId as String
                                    updateGameStream()
                                }
                                href = "#gamestream"
                            }
                            +"gameId :: ${e.eventId}"
                        }
                    } else {
                        p {
                            +"eventId :: ${e.eventId}"
                        }
                    }
                    pre { +JSON.stringify(e.payload, null, 4) }
                }
            }
        }
    }

    private fun updateGameStream(){
        eventApi.events({ data -> setState { gameEvents = data } }, selectedGameId)
        eventApi.dsl({ data -> setState { gameDsl = data } }, selectedGameId)
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

fun RBuilder.cards(cards: Cards, backOfCard: dynamic = backOfCardImage()) = child(CardComponent::class) {
    attrs.cards = cards
    attrs.backOfCard = backOfCard
}

interface CardProps : RProps {
    var backOfCard: dynamic
    var cards: Cards
}

class CardComponent : RComponent<CardProps, RState>() {
    override fun RBuilder.render() {
        val visibleCards = props.cards.visibleCards
        if (visibleCards.isEmpty()) {
            (1..props.cards.numberOfCards).map {
                img(src = props.backOfCard){ }
            }
        } else {
            visibleCards.map {
                card -> img(src = cardImage(card)) { }
            }
        }
    }
}