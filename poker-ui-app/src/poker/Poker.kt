package poker

import axios.AxiosConfigSettings
import axios.axios
import kotlinext.js.jsObject
import kotlinx.html.InputType
import kotlinx.html.label
import react.*
import react.dom.*


enum class Suit { DIAMOND, SPADES, HEART, CLUB }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
data class Card(val value: Value, val suit: Suit)
data class Player(val name: String, val cards: List<Card> = listOf())
data class Table(val players: Array<Player> = emptyArray(), val winner: Player? = null)
data class TableData(val table: Table)
data class GraphqlResponse(val data: TableData)

fun RBuilder.pokerTable() = child(PokerTableRepresentation::class){}

class PokerTableRepresentation : RComponent<RProps, RState>() {
    private var table: Table = Table()

    private fun createHeaders(): dynamic {
        val postHeaders = js("({})")

        postHeaders["Content-Type"] = "application/json"
        postHeaders["test"] = "this123"

        //console.log(postHeaders)

        return postHeaders
    }

    override fun componentDidMount(){
        val config: AxiosConfigSettings = jsObject {
            method = "post"
            url = "/graphql"
            timeout = 3000
            headers = createHeaders()
            data = """
                    {
                      "query": "query tableQuery {\n  table {\n    players {\n      name\n    }\n  }\n}",
                      "variables": {
                        "name": "yves"
                      },
                      "operationName": "tableQuery"
                    }
                    """.trimIndent()
        }

        axios<GraphqlResponse>(config).then { response ->
            setState {
                console.log(response.data.data.table)
                table = response.data.data.table
            }
            console.log(response)
        }
    }

    override fun RBuilder.render() {
        h1 { + "Poker table" }

        joinGame()
        playerList(table)
    }

}

interface PlayerListProps : RProps {
    var table: Table
}

fun RBuilder.playerList(table: Table) = child(PlayerList::class){ attrs.table = table }

class PlayerList : RComponent<PlayerListProps, RState>() {
    override fun RBuilder.render() {
        ul {
            props.table.players.map { player -> li { + player.name } }
        }
    }
}

fun RBuilder.joinGame() = child(JoinGame::class){}

class JoinGame : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        p { + "Join the game" }
        input(type = InputType.text){
            attrs {
                placeholder = "enter player name"
            }
        }
        input(type = InputType.button){
            attrs {
                value = "Join game"
            }
        }
    }

}
