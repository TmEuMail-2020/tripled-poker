package app

import react.*
import react.dom.*
import logo.*
import ticker.*
import poker.pokerTable

class App : RComponent<RProps, RState>() {

    override fun RBuilder.render() {
        div("App-header") {
            logo()
        }
        p("App-ticker") {
            ticker()
        }
        pokerTable()
    }
}

fun RBuilder.app() = child(App::class) {}