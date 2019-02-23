package poker

import axios.axios
import deck.cardImage
import kotlinext.js.jsObject
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*

enum class Suit { DIAMOND, SPADES, HEART, CLUB }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
data class Card(val value: String, val suit: String){
    val typedValue = Value.valueOf(value)
    val typedSuit = Suit.valueOf(suit)
}
data class Player(val name: String, val cards: Array<Card> = emptyArray())
data class Table(val players: Array<Player> = emptyArray(), val winner: Player? = null)

fun RBuilder.pokerTable() = child(PokerTableRepresentation::class) {}

class PokerTableRepresentation : RComponent<RProps, RState>() {
    private val pokerApi = PokerApi()
    private var table: Table = Table()

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

        joinGame { playerName -> pokerApi.joinTable(playerName, ::updateTable) }
        input(type = InputType.button) {
            attrs {
                value = "Play round"
                onClickFunction = { event: Event ->
                    pokerApi.playRound(::updateTable)
                }
            }
        }
        h1 { + "Winner: ${table.winner?.name}"}
        playerList(table)
    }

}

interface PlayerListProps : RProps {
    var table: Table
}

fun RBuilder.playerList(table: Table) = child(PlayerList::class) { attrs.table = table }

class PlayerList : RComponent<PlayerListProps, RState>() {
    override fun RBuilder.render() {
        ul {
            props.table.players.map { player ->
                li {
                    p { + player.name }
                    card(player.cards[0])
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
        p { +"Join the game" }
        input(type = InputType.text) {
            attrs {
                placeholder = "enter player name"
                onChangeFunction = { event: Event ->
                    playerName = (event.target as HTMLInputElement).value
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

fun RBuilder.card(card: Card) = child(CardComponent::class) { attrs.card = card }

interface CardProps : RProps {
    var card: Card
}

class CardComponent : RComponent<CardProps, RState>() {

    override fun RBuilder.render() {
        console.log(props.card)
        //p { + Suit.valueOf(props.card.suit).name }
        img(src = cardImage(props.card)) { }
    }
}


data class TableData(val table: Table)
data class GraphqlResponse(val data: TableData)

class PokerApi {
    private fun createHeaders(): dynamic {
        val postHeaders = js("({})")
        postHeaders["Content-Type"] = "application/json"
        return postHeaders
    }

    fun getTable(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("getTable")
            }).then { response ->
                f.invoke(response.data.data.table)
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

    fun playRound(f: (Table) -> Unit) =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("playRound")
            }).then { response ->
                f.invoke(response.data.data.table)
            }


    private fun query(operation: String, playerName: String = "") = """
        {
          "query": "mutation playRound {\n  table: startRound {\n    players {\n      name\n      cards {\n        suit\n        value\n      }\n    }\n    winner {\n      name\n      cards {\n        suit\n        value\n      }\n    }\n  }\n}\n\nmutation joinTable(${'$'}name: String!) {\n  table: joinTable(name: ${'$'}name) {\n    players {\n      name\n      cards {\n        suit\n        value\n      }\n    }\n    winner {\n      name\n      cards {\n        suit\n        value\n      }\n    }\n  }\n}\n\nquery getTable {\n  table {\n    players {\n      name\n      cards {\n        suit\n        value\n      }\n    }\n    winner {\n      name\n      cards {\n        suit\n        value\n      }\n    }\n  }\n}\n",
          "variables": {
            "name": "$playerName"
          },
          "operationName": "$operation"
        }
        """.trimIndent()


}