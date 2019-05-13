package poker

import axios.axios
import deck.backOfCardImage
import deck.cardImage
import kotlinext.js.jsObject
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyPressFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*
import kotlin.js.Promise

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
                value = "Play round"
                onClickFunction = { event: Event ->
                    pokerApi.playRound(::updateTable)
                }
            }
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
                value = "flop"
                onClickFunction = { event: Event ->
                    pokerApi.flop(::updateTable)
                }
            }
        }
        input(type = InputType.button) {
            attrs {
                value = "turn"
                onClickFunction = { event: Event ->
                    pokerApi.turn(::updateTable)
                }
            }
        }
        input(type = InputType.button) {
            attrs {
                value = "river"
                onClickFunction = { event: Event ->
                    pokerApi.river(::updateTable)
                }
            }
        }
        input(type = InputType.button) {
            attrs {
                value = "winner"
                onClickFunction = { event: Event ->
                    pokerApi.winner(::updateTable)
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


data class TableData(val table: Table)
data class GraphqlResponse(val data: TableData)

class PokerApi(var playerName: String = "") {
    private fun createHeaders(): dynamic {
        val postHeaders = js("({})")
        postHeaders["Content-Type"] = "application/json"
        return postHeaders
    }

    fun joinTable(playerName: String, f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("joinTable", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }

    fun getTable(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("getTable", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }

    fun check(playerName: String, f: (Table) -> Unit): Promise<Unit> {
        this.playerName = playerName
        return axios<GraphqlResponse>(jsObject {
            method = "post"
            url = "/graphql"
            timeout = 3000
            headers = createHeaders()
            data = query("check", playerName)
        }).then { response ->
            f.invoke(response.data.data.table)
        }
    }

    fun playRound(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("playRound", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }


    fun check(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("check", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }


    fun turn(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("turn", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }


    fun flop(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("flop", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }


    fun river(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("river", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }


    fun winner(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("winner", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }

    private fun query(operation: String, playerName: String = "") = """
        {
          "query": "mutation playRound(${'$'}name: String!) {\n  table: startRound(name:${'$'}name) {\n    players {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n    winner {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n  }\n}\n\nmutation joinTable(${'$'}name: String!) {\n  table: joinTable(name:${'$'}name) {\n    players {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n    winner {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n  }\n}\n\nmutation check(${'$'}name: String!) {\n  table: check(name:${'$'}name) {\n    players {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n    winner {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n  }\n}\n\nmutation flop(${'$'}name: String!) {\n  table: flop(name:${'$'}name) {\n    players {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n    winner {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n  }\n}\n\nmutation river(${'$'}name: String!) {\n  table: river(name:${'$'}name) {\n    players {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n    winner {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n  }\n}\n\nmutation turn(${'$'}name: String!) {\n  table: turn(name:${'$'}name) {\n    players {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n    winner {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n  }\n}\n\n\nmutation winner(${'$'}name: String!) {\n  table: winner(name:${'$'}name) {\n    players {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n    winner {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n  }\n}\n\nquery getTable(${'$'}name: String!) {\n  table(name:${'$'}name) {\n    players {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n    winner {\n      name\n      cards {\n        numberOfCards\n        visibleCards {\n          suit\n          value\n        }\n      }\n    }\n  }\n}\n",
          "variables": {
            "name": "$playerName"
          },
          "operationName": "$operation"
        }
        """.trimIndent()


}