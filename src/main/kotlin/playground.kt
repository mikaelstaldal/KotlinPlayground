fun f(n: Int): Int = when (n) {
    0 -> 2
    1 -> 1
    else -> f(n - 1) + f(n - 2)
}

fun exercise() {
    var s = ""
    for (i in 1..5) {
        s += f(i).toString()
    }
    println(s)
}
