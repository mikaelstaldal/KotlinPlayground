package async

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.*
import java.net.URL
import java.time.LocalTime
import kotlin.system.measureTimeMillis

fun log(msg: String) {
    println("${LocalTime.now()} $msg")
}

class Weather(@Json(name = "Temp") val temperature: Array<String>)

class Airport(
    @Json(name = "IATA") val code: String,
    @Json(name = "Name") val name: String,
    @Json(name = "Delay") val delay: Boolean,
    @Json(name = "Weather") val weather: Weather
) {

    companion object {
        fun getAirportData(code: String): Airport? {
            val url = "https://soa.smext.faa.gov/asws/api/airport/status/$code"
            return Klaxon().parse<Airport>(URL(url).readText())
        }
    }
}

fun rynSync() {
    val format = "%-10s%-20s%-10s"
    log(String.format(format, "Code", "Temperature", "Delay"))

    val time = measureTimeMillis {
        val airportCodes = listOf("LAX", "SFO", "PDX", "SEA")

        val airportData: List<Airport> =
            airportCodes.mapNotNull { anAirportCode ->
                Airport.getAirportData(anAirportCode)
            }

        airportData.forEach { anAirport ->
            log(
                String.format(
                    format, anAirport.code,
                    anAirport.weather.temperature[0], anAirport.delay
                )
            )
        }
    }

    log("Time taken $time ms")
}

fun runAsync() = runBlocking {
    val format = "%-10s%-20s%-10s"
    log(String.format(format, "Code", "Temperature", "Delay"))

    val time = measureTimeMillis {
        val airportCodes = listOf("LAX", "SFO", "PDX", "SEA")

        val airportData: List<Deferred<Airport?>> =
            airportCodes.map { anAirportCode ->
                async(Dispatchers.IO) {
                    Airport.getAirportData(anAirportCode)
                }
            }

        airportData
            .mapNotNull { it.await() }
            .forEach {
                log(String.format(format, it.code, it.weather.temperature[0], it.delay))
            }
    }

    log("Time taken $time ms")
}

fun launchErrHandle() = runBlocking {
    val handler = CoroutineExceptionHandler { context, ex ->
        log(
            "Caught: ${context[CoroutineName]} ${ex.message?.substring(0..28)}"
        )
    }

    try {
        val airportCodes = listOf("LAX", "SF-", "PD-", "SEA")

        log("start")
        val jobs: List<Job> = airportCodes.map { anAirportCode ->
            launch(Dispatchers.IO + CoroutineName(anAirportCode) + handler + SupervisorJob()) {
                val airport = Airport.getAirportData(anAirportCode)
                log("${airport?.code} delay: ${airport?.delay}")
            }
        }
        log("middle")
        jobs.forEach { it.join() }
        jobs.forEach { log("Cancelled: ${it.isCancelled}") }
        log("end")
    } catch (ex: Exception) {
        log("ERROR: ${ex.message}")
        ex.printStackTrace()
    }
}

fun asyncErr() = runBlocking {
    val airportCodes = listOf("LAX", "SF-", "PD-", "SEA")

    log("start")
    val airportData = airportCodes.map { anAirportCode ->
        async(Dispatchers.IO + SupervisorJob()) {
            Airport.getAirportData(anAirportCode)
        }
    }
    log("middle")
    for (anAirportData in airportData) {
        try {
            val airport = anAirportData.await()

            log("${airport?.code} ${airport?.delay}")
        } catch (ex: Exception) {
            log("Error: ${ex.message?.substring(0..28)}")
        }
    }
    log("end")
}

suspend fun compute(checkActive: Boolean) = coroutineScope {
    var count = 0L
    val max = 10000000000

    while (if (checkActive) {
            isActive
        } else (count < max)
    ) {
        count++
    }
    if (count == max) {
        log("compute, checkActive $checkActive ignored cancellation")
    } else {
        log("compute, checkActive $checkActive bailed out early")
    }
}

fun getResponse(code: Int, delayMs: Int): String {
    val s = URL("http://httpstat.us/${code}?sleep=$delayMs").readText()
    log("Got: $s")
    return s
}

suspend fun fetchResponse(code: Int, delayMs: Int, callAsync: Boolean) = coroutineScope {
    try {
        val response = if (callAsync) {
            async { getResponse(code, delayMs) }.await()
        } else {
            getResponse(code, delayMs)
        }

        log(response)
    } catch (ex: CancellationException) {
        log("fetchResponse called with callAsync $callAsync: ${ex.message}")
    }
}

fun cancelAndSuspension() {
    log("before runBlocking")
    runBlocking {
        val job = launch(Dispatchers.Default) {
            launch { compute(checkActive = false) }
            launch { compute(checkActive = true) }
            launch { fetchResponse(200, 2000, callAsync = false) }
            launch { fetchResponse(200, 4000, callAsync = true) }
        }

        log("Let them run...")
        delay(1000)
        log("OK, that's enough, cancel")
        job.cancel()
        log("end of runBlocking")
    }
    log("after runBlocking")
}

suspend fun doWork(id: Int, sleep: Long) = coroutineScope {
    try {
        log("$id: entered $sleep")
        delay(sleep)
        log("$id: finished nap $sleep")

        withContext(NonCancellable) {
            log("$id: do not disturb, please")
            delay(5000)
            log("$id: OK, you can talk to me now")
        }

        log("$id: outside the restricted context, isActive: $isActive")
    } catch (ex: CancellationException) {
        log("$id: doWork($sleep) was cancelled")
    }
}

fun doNotDisturb() {
    runBlocking {
        val job = launch(Dispatchers.Default) {
            launch { doWork(1, 3000) }
            launch { doWork(2, 1000) }
        }

        delay(2000)
        job.cancel()
        log("cancelling")
        job.join()
        log("done")
    }
}

fun bidirectionalCancellation() {
    runBlocking {
        val handler = CoroutineExceptionHandler { _, ex ->
            log("Exception handled: ${ex.message}")
        }

        val job = launch(Dispatchers.IO + handler) {
            supervisorScope {
                launch { fetchResponse(200, 5000, true) }
                launch { fetchResponse(202, 1000, true) }
                launch { fetchResponse(404, 2000, true) }
            }
        }
        delay(4000)
        job.cancel()
        log("joining")
        job.join()
        log("end of runBlocking")
    }
    log("after runBlocking")
}

fun timeout() {
    runBlocking {
        val handler = CoroutineExceptionHandler { _, ex ->
            log("Exception handled: ${ex.message}")
        }

        val job = launch(Dispatchers.IO + handler) {
            withTimeout(3000) {
                launch { fetchResponse(200, 5000, true) }
                launch { fetchResponse(201, 1000, true) }
                launch { fetchResponse(202, 2000, true) }
            }
        }
        log("joining")
        job.join()
        log("end of runBlocking")
    }
    log("after runBlocking")
}

fun main() {
    timeout()
}
