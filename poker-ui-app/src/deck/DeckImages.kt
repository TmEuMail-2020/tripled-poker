package deck

import poker.Card
import poker.Suit
import poker.Value


fun cardImage(card: Card): dynamic
        = if (card == null) backOfCardImage() else when(Suit.valueOf(card.suit)){
    Suit.DIAMONDS -> when (Value.valueOf(card.value)){
        Value.TWO -> card2D
        Value.THREE -> card3D
        Value.FOUR -> card4D
        Value.FIVE -> card5D
        Value.SIX -> card6D
        Value.SEVEN -> card7D
        Value.EIGHT -> card8D
        Value.NINE -> card9D
        Value.TEN -> cardTD
        Value.JACK -> cardJD
        Value.QUEEN -> cardQD
        Value.KING -> cardKD
        Value.ACE -> cardAD
    }
    Suit.SPADES -> when (Value.valueOf(card.value)){
        Value.TWO -> card2S
        Value.THREE -> card3S
        Value.FOUR -> card4S
        Value.FIVE -> card5S
        Value.SIX -> card6S
        Value.SEVEN -> card7S
        Value.EIGHT -> card8S
        Value.NINE -> card9S
        Value.TEN -> cardTS
        Value.JACK -> cardJS
        Value.QUEEN -> cardQS
        Value.KING -> cardKS
        Value.ACE -> cardAS
    }
    Suit.HEARTS -> when (Value.valueOf(card.value)){
        Value.TWO -> card2H
        Value.THREE -> card3H
        Value.FOUR -> card4H
        Value.FIVE -> card5H
        Value.SIX -> card6H
        Value.SEVEN -> card7H
        Value.EIGHT -> card8H
        Value.NINE -> card9H
        Value.TEN -> cardTH
        Value.JACK -> cardJH
        Value.QUEEN -> cardQH
        Value.KING -> cardKH
        Value.ACE -> cardAH
    }
    Suit.CLUBS -> when (Value.valueOf(card.value)){
        Value.TWO -> card2H
        Value.THREE -> card3H
        Value.FOUR -> card4H
        Value.FIVE -> card5H
        Value.SIX -> card6H
        Value.SEVEN -> card7H
        Value.EIGHT -> card8H
        Value.NINE -> card9H
        Value.TEN -> cardTH
        Value.JACK -> cardJH
        Value.QUEEN -> cardQH
        Value.KING -> cardKH
        Value.ACE -> cardAH
    }
}

fun backOfCardImage() = card1B

@JsModule("src/deck/1B.svg")
external val card1B: dynamic
@JsModule("src/deck/2C.svg")
external val card2C: dynamic
@JsModule("src/deck/2J.svg")
external val card2J: dynamic
@JsModule("src/deck/3D.svg")
external val card3D: dynamic
@JsModule("src/deck/4C.svg")
external val card4C: dynamic
@JsModule("src/deck/4S.svg")
external val card4S: dynamic
@JsModule("src/deck/5H.svg")
external val card5H: dynamic
@JsModule("src/deck/6D.svg")
external val card6D: dynamic
@JsModule("src/deck/7C.svg")
external val card7C: dynamic
@JsModule("src/deck/7S.svg")
external val card7S: dynamic
@JsModule("src/deck/8H.svg")
external val card8H: dynamic
@JsModule("src/deck/9D.svg")
external val card9D: dynamic
@JsModule("src/deck/AC.svg")
external val cardAC: dynamic
@JsModule("src/deck/AS.svg")
external val cardAS: dynamic
@JsModule("src/deck/JH.svg")
external val cardJH: dynamic
@JsModule("src/deck/KD.svg")
external val cardKD: dynamic
@JsModule("src/deck/QC.svg")
external val cardQC: dynamic
@JsModule("src/deck/QS.svg")
external val cardQS: dynamic
@JsModule("src/deck/TH.svg")
external val cardTH: dynamic
@JsModule("src/deck/1J.svg")
external val card1J: dynamic
@JsModule("src/deck/2D.svg")
external val card2D: dynamic
@JsModule("src/deck/2S.svg")
external val card2S: dynamic
@JsModule("src/deck/3H.svg")
external val card3H: dynamic
@JsModule("src/deck/4D.svg")
external val card4D: dynamic
@JsModule("src/deck/5C.svg")
external val card5C: dynamic
@JsModule("src/deck/5S.svg")
external val card5S: dynamic
@JsModule("src/deck/6H.svg")
external val card6H: dynamic
@JsModule("src/deck/7D.svg")
external val card7D: dynamic
@JsModule("src/deck/8C.svg")
external val card8C: dynamic
@JsModule("src/deck/8S.svg")
external val card8S: dynamic
@JsModule("src/deck/9H.svg")
external val card9H: dynamic
@JsModule("src/deck/AD.svg")
external val cardAD: dynamic
@JsModule("src/deck/JC.svg")
external val cardJC: dynamic
@JsModule("src/deck/JS.svg")
external val cardJS: dynamic
@JsModule("src/deck/KH.svg")
external val cardKH: dynamic
@JsModule("src/deck/QD.svg")
external val cardQD: dynamic
@JsModule("src/deck/TC.svg")
external val cardTC: dynamic
@JsModule("src/deck/TS.svg")
external val cardTS: dynamic
@JsModule("src/deck/2B.svg")
external val card2B: dynamic
@JsModule("src/deck/2H.svg")
external val card2H: dynamic
@JsModule("src/deck/3C.svg")
external val card3C: dynamic
@JsModule("src/deck/3S.svg")
external val card3S: dynamic
@JsModule("src/deck/4H.svg")
external val card4H: dynamic
@JsModule("src/deck/5D.svg")
external val card5D: dynamic
@JsModule("src/deck/6C.svg")
external val card6C: dynamic
@JsModule("src/deck/6S.svg")
external val card6S: dynamic
@JsModule("src/deck/7H.svg")
external val card7H: dynamic
@JsModule("src/deck/8D.svg")
external val card8D: dynamic
@JsModule("src/deck/9C.svg")
external val card9C: dynamic
@JsModule("src/deck/9S.svg")
external val card9S: dynamic
@JsModule("src/deck/AH.svg")
external val cardAH: dynamic
@JsModule("src/deck/JD.svg")
external val cardJD: dynamic
@JsModule("src/deck/KC.svg")
external val cardKC: dynamic
@JsModule("src/deck/KS.svg")
external val cardKS: dynamic
@JsModule("src/deck/QH.svg")
external val cardQH: dynamic
@JsModule("src/deck/TD.svg")
external val cardTD: dynamic

