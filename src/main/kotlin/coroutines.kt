package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.Executors

suspend fun task1() {
    println("start task1 in Thread ${Thread.currentThread().name}")
    delay(1000)
    println("end task1 in Thread ${Thread.currentThread().name}")
}

suspend fun task2() {
    println("start task2 in Thread ${Thread.currentThread().name}")
    delay(1000)
    println("end task2 in Thread ${Thread.currentThread().name}")
}

fun main1() {
    println("start")

    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher().use { context ->
        runBlocking {
            launch(context = context, start = CoroutineStart.UNDISPATCHED) { task1() }
            launch { task2() }

            println("called task1 and task2 from ${Thread.currentThread().name}")
        }
    }

    println("done")
}

fun main2() {
    println("start")

    runBlocking(CoroutineName("top")) {
        println("starting in Thread ${Thread.currentThread().name}")
        // withContext(Dispatchers.Default) { task1() }
        launch(Dispatchers.Default + CoroutineName("task 1")) { task1() }
        launch(CoroutineName("task 2")) { task2() }

        println("ending in Thread ${Thread.currentThread().name}")
    }

    println("done")
}

fun main() {
    runBlocking {
        val count: Deferred<Int> = async(Dispatchers.Default) {
            println("fetching in ${Thread.currentThread()}")
            Runtime.getRuntime().availableProcessors()
        }

        println("Called the function in ${Thread.currentThread()}")

        println("Number of cores is ${count.await()}")
    }
}
