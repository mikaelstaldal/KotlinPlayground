package sealed

enum class Suit(val symbol: Char) {
    CLUBS('\u2663'),
    DIAMONDS('\u2666'),
    HEARTS('\u2665') {
        override fun display(): String = "${super.display()} $symbol"
    },
    SPADES('\u2660');

    open fun display() = "$symbol $name"
}

sealed class Card(val suit: Suit)

class Ace(suit: Suit): Card(suit)

class King(suit: Suit): Card(suit) {
    override fun toString(): String = "${suit.symbol}King"
}

class Queen(suit: Suit): Card(suit) {
    override fun toString(): String = "${suit.symbol}Queen"
}

class Jack(suit: Suit): Card(suit) {
    override fun toString(): String = "${suit.symbol}Jack"
}

class Pip(suit: Suit, val number: Int): Card(suit) {
    init {
        if (number !in 2..10) {
            throw IllegalArgumentException("Pip has to be between 2 and 10")
        }
    }
}

fun process(card: Card) = when (card) {
    is Ace -> "${card.suit.symbol}${card.javaClass.simpleName}"
    is King, is Queen, is Jack -> "$card"
    is Pip -> "${card.suit.symbol}${card.number}"
}

fun main() {
    for (suit in Suit.values()) {
        println(suit.display())
    }

    println(process(Ace(Suit.DIAMONDS)))
    println(process(Queen(Suit.CLUBS)))
    println(process(Pip(Suit.SPADES, 2)))
    println(process(Pip(Suit.HEARTS, 6)))
}
