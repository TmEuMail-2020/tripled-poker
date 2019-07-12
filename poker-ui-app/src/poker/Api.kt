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
            table: startRound(name: ${'$'}name) {
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
                data = query("getTable", playerName)
            }).then { response ->
                f.invoke(response.data.data.table)
            }

    fun joinTable(playerName: String, f: (Table) -> Unit): Promise<Unit> {
        this.playerName = playerName
        return axios<GraphqlResponse>(jsObject {
            method = "post"
            url = "/graphql"
            timeout = 3000
            headers = createHeaders()
            data = query("joinTable", playerName)
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

    fun check(f: (Table) -> Unit)  =
            axios<GraphqlResponse>(jsObject {
                method = "post"
                url = "/graphql"
                timeout = 3000
                headers = createHeaders()
                data = query("check", playerName)
            }).then { response ->

                if (response.data.errors != null && response.data.errors.isNotEmpty()){
                    window.alert(response.data.errors[0].message)
                } else {
                    f.invoke(response.data.data.table)
                }
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