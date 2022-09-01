import java.lang.RuntimeException

(fun() {
    var factor = 2

    val doubled = listOf(1, 2).map { it * factor }
    val doubledAlso = sequenceOf(1, 2).map { it * factor }

    factor = 0

    val doubled2 = listOf(1, 2).map { it * factor }

    doubled.forEach { println(it) }
    doubledAlso.forEach { println(it) }
    doubled2.forEach { println(it) }
})()

fun invokeWith(n: Int, action: (Int) -> Unit) {
    println("enter invokeWith $n")
    action(n)
    println("exit invokeWith $n")
}

(fun() {
    (1..3).forEach { i ->
        println("in forEach for $i")
        if (i == 2) return
        invokeWith(i) {
            println("enter for $it")
            if (it == 2) {
                return@invokeWith
            }
            println("exit for $it")
        }
    }
    println("end of caller")
})()
println("after return from caller")

inline fun invokeTwo(
    n: Int,
    action1: (Int) -> Unit,
    noinline action2: (Int) -> Unit,
    crossinline action3: (Int) -> Unit
)
        : (Int) -> Unit {
    println("enter invokeTwo $n")
    action1(n)
    action2(n)
    println("exit invokeTwo $n")
    return { input: Int -> action2(input); action3(input) }
}

fun report(n: Int) {
    println("")
    print("called with $n, ")

    val stackTrace = RuntimeException().stackTrace

    println("Stack depth: ${stackTrace.size}")
    println("Partial listing of the stack:")
    stackTrace.take(4).forEach(::println)
}

fun callInvokeTwo() {
    invokeTwo(1, { i ->
        if (i == 1) {
            return
        }; report(i)
    }, { i -> report(i) },
        { i -> report(i) })
}
callInvokeTwo()

listOf(1, 2, 3).reduce { total, a -> a + total }

fun isPrime(n: Long) = n > 1 && (2 until n).none { i -> n % i == 0L }

tailrec fun nextPrime(n: Long): Long = if (isPrime(n + 1)) n + 1 else nextPrime(n + 1)

val primes1 = generateSequence(5, ::nextPrime)
println(primes1.take(6).toList())

val primes2 = sequence {
    var i: Long = 0
    while (true) {
        i++
        if (isPrime(i)) {
            yield(i)
        }
    }
}
println(primes2.drop(2).take(6).toList())
