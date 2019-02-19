package app

import react.*
import react.dom.*
import logo.*
import ticker.*
import axios.*

class App : RComponent<RProps, RState>() {

    override fun RBuilder.render() {
        div("App-header") {
            logo()
            h2 {
                +"Welcome to React with Kotlin"
            }
        }
        p("App-intro") {
            +"To get started, edit "
            code { +"app/App.kt" }
            +" and save to reload."
        }
        p("App-ticker") {
            ticker()
        }
        test()
        pokerTable()
    }
}

fun RBuilder.app() = child(App::class) {}

fun RBuilder.test() = child(AxiosExample::class){}

data class User(val name: String)

class AxiosExample : RComponent<RProps, RState>() {
    private var persons: Array<User> = arrayOf()

    override fun componentDidMount(){
        Axios.get<dynamic>("https://jsonplaceholder.typicode.com/users")
                .then { result ->
                    setState {
                        persons = result.data
                    }
                }
    }

    override fun RBuilder.render() {
        h1 { + "testing" }

        ul {
            console.log(persons)
            persons.map { person -> li { + person.name } }
        }
    }
}



enum class Suit { DIAMOND, SPADES, HEART, CLUB }
enum class Value { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
data class Card(val value: Value, val suit: Suit)
data class Player(val name: String, val cards: List<Card> = listOf())
data class Table(val players: List<Player>, val winner: Player? = null)

fun RBuilder.pokerTable() = child(PokerTableRepresentation::class){}

class PokerTableRepresentation : RComponent<RProps, RState>() {
    private var table: Table = Table(emptyList())

    override fun componentDidMount(){
        Axios.post<Table>("/graphql",
                """
{
  "query": "query tableQuery {\n  table {\n    players {\n      name\n    }\n  }\n}",
  "variables": {
    "name": "yves"
  },
  "operationName": "tableQuery"
}
                """.trimIndent(),
                object : AxiosRequestConfig {
                    override var httpAgent: Any? = "shizzle agent"
                    override var headers: Any? = createHeaders()

                    private fun createHeaders(): dynamic {
                        val postHeaders = js("({})")

                        postHeaders["Content-Type"] = "application/json"
                        postHeaders["test"] = "this123"

                        console.log(postHeaders)

                        return postHeaders
                    }
                })
                .then { result ->
                    setState {
                        console.log(result.data)
                        table = result.data
                    }
                }
    }

    override fun RBuilder.render() {
        h1 { + "Poker table" }

        ul {
            console.log(table)
            table.players.map { player -> li { + player.name } }
        }
    }

}