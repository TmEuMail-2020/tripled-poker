package app.logo

import react.RBuilder
import react.dom.div
import react.dom.h1
import react.dom.img

@JsModule("src/deck/AS.svg")
external val aceOfSpades: dynamic

fun RBuilder.logo() {
    div("Logo") {
        div {
            img(alt = "Ace of spades", src = aceOfSpades, classes = "Logo-poker") {}
        }
        div {
            h1(classes = "Logo-text") { + "Kontich holdem" }
        }
    }
}