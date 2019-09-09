package poker

import axios.axios
import kotlinext.js.jsObject
import kotlin.browser.window
import kotlin.js.Promise

data class TableData(val table: Table)
data class GraphqlResponse(val data: TableData, val errors: Array<dynamic>)

class PokerApi(var playerName: String = "") {

    private val graphql = """
        mutation playRound(${'$'}name: String!) {
            table: startGame(name: ${'$'}name) {
                players {
                    name
                    cards {
                        numberOfCards
                        visibleCards {
                            suit
                            value
                        }
                    }
                }
                winner {
                    name
                    cards {
                        numberOfCards
                        visibleCards {
                            suit
                            value
                        }
                    }
                }
            }
        }
        
        mutation joinTable(${'$'}name: String!) {
            table: joinTable(name: ${'$'}name) {
                players {
                    name
                    cards {
                        numberOfCards
                        visibleCards {
                            suit
                            value
                        }
                    }
                }
                winner {
                    name
                    cards {
                        numberOfCards
                        visibleCards {
                            suit
                            value
                        }
                    }
                }
            }
        }
        
        mutation check(${'$'}name: String!) {
            table: check(name: ${'$'}name) {
                players {
                    name
                    cards {
                        numberOfCards
                        visibleCards {
                            suit
                            value
                        }
                    }
                }
                winner {
                    name
                    cards {
                        numberOfCards
                        visibleCards {
                            suit
                            value
                        }
                    }
                }
            }
        }
        
        query getTable(${'$'}name: String!) {
            table(name: ${'$'}name) {
                players {
                    name
                    cards {
                        numberOfCards
                        visibleCards {
                            suit
                            value
                        }
                    }
                }
                winner {
                    name
                    cards {
                        numberOfCards
                        visibleCards {
                            suit
                            value
                        }
                    }
                }
            }
        }
    """.trimIndent()
            .replace("\n", "\\n")
            .replace("\t", "\\t")

    fun getTable(f: (Table) -> Unit) =
            post(f, "getTable")

    fun joinTable(playerName: String, f: (Table) -> Unit): Promise<Unit> {
        this.playerName = playerName
        return post(f, "joinTable")
    }

    fun playRound(f: (Table) -> Unit) = post(f, "playRound")

    fun check(f: (Table) -> Unit)  = post(f, "check")

    private fun post(f: (Table) -> Unit, operation: String): Promise<Unit> {
        return axios<GraphqlResponse>(jsObject {
            method = "post"
            url = "/graphql"
            timeout = 3000
            headers = createHeaders()
            data = query(operation, playerName)
        }).then { response ->
            if (response.data.errors != null && response.data.errors.isNotEmpty()){
                window.alert(response.data.errors[0].message)
            } else {
                f.invoke(response.data.data.table)
            }
        }
    }

    private fun createHeaders(): dynamic {
        val postHeaders = js("({})")
        postHeaders["Content-Type"] = "application/json"
        return postHeaders
    }

    private fun query(operation: String, playerName: String = "") = """
        {
          "query": "$graphql",
          "variables": {
            "name": "$playerName"
          },
          "operationName": "$operation"
        }
        """.trimIndent()
}

class EventStreamApi {

    fun dsl(f: (String) -> Unit, eventId: String){
        axios<String>(jsObject {
            method = "get"
            url = "/dsl/$eventId"
            timeout = 3000
        }).then { response ->
            f.invoke(response.data)
        }
    }

    fun events(f: (Array<dynamic>) -> Unit, eventId: String){
        axios<Array<dynamic>>(jsObject {
            method = "get"
            url = "/events/$eventId"
            timeout = 3000
        }).then { response ->
            f.invoke(response.data)
        }
    }
}