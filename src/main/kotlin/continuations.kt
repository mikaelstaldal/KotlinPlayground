package continuations

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Compute {
  fun compute1(n: Long): Long = n * 2
  suspend fun compute2(n: Long): Long {
    val factor = 2
    println("$n received : Thread: ${Thread.currentThread().name}")
    delay(n * 1000)
    val result = n * factor
    println("$n, returning $result: Thread: ${Thread.currentThread().name}")
    return result
  }
}

fun main1() {
    runBlocking {
        val compute = Compute()

        launch(Dispatchers.Default) {
            compute.compute2(2)
        }
        launch(Dispatchers.Default) {
            compute.compute2(1)
        }
    }
}

fun primes(start: Int): Sequence<Int> = sequence {
  println("Starting to look")
  var index = start

  while (true) {
    if (index > 1 && (2 until index).none { i -> index % i == 0 }) {
      yield(index)
      println("Generating next after $index")
    }

    index++
  }
}

fun main2() {
    for (prime in primes(start = 17)) {
        println("Received $prime")
        if (prime > 30) break
    }
}

operator fun ClosedRange<String>.iterator(): Iterator<String> = iterator {
  val next = StringBuilder(start)
  val last = endInclusive

  while (last >= next.toString() && last.length >= next.length) {
    val result = next.toString()

    val lastCharacter = next.last()

    if (lastCharacter < Char.MAX_VALUE) {
      next.setCharAt(next.length - 1, lastCharacter + 1)
    } else {
      next.append(Char.MIN_VALUE)
    }

    yield(result)
  }
}

fun main() {
    for (word in "hell".."help") {
        print("$word, ")
    }
}
