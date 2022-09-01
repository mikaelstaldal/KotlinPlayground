import java.lang.Exception
import kotlin.IllegalArgumentException

fun drawCircle() {
    val circle = object {
        val x = 10
        val y = 20
        val radius = 30
    }

    println("${circle.x},${circle.y} ${circle.radius}")
}

drawCircle()

fun createRunnable(): Runnable = object : Runnable, AutoCloseable {
    override fun run() {
        println("run")
    }

    override fun close() {
        println("close")
    }
}

val runner = createRunnable()
runner.run()
(runner as AutoCloseable).close()

object Runner : Runnable, AutoCloseable {
    override fun run() {
        println("run run")
    }

    override fun close() {
        println("close close")
    }
}

Runner.run()
Runner.close()

class Car private constructor(val yearOfMake: Int, theColor: String) {
    var fuelLevel = 100

    var color = theColor
        set(value) {
            if (value.isBlank()) {
                throw IllegalArgumentException("no color")
            }
            field = value
        }

    init {
        if (theColor.isBlank()) {
            throw IllegalArgumentException("no color")
        }
    }

    override fun toString(): String {
        return "Car(yearOfMake=$yearOfMake, fuelLevel=$fuelLevel, color='$color')"
    }

    companion object {
        var numberOfCars = 0

        fun create(yearOfMake: Int, color: String): Car {
            numberOfCars++
            return Car(yearOfMake, color)
        }
    }
}

try {
    val car = Car.create(2019, "Red")
    println(car)
    println(car::class)
    car.color = "Green"
    car.fuelLevel--
    println(car)
    car.fuelLevel -= 10
    car.color = ""
    println(car)
} catch (e: Exception) {
    println(e)
}

@JvmInline
value class SSN(val id: String)

class PriorityPair<T: Comparable<T>>(member1: T, member2: T) {
    val first: T
    val second: T

    init {
        if (member1 >= member2) {
            first = member1
            second = member2
        } else {
            first = member2
            second = member1
        }
    }

    override fun toString(): String {
        return "PriorityPair(first=$first, second=$second)"
    }
}

println(PriorityPair(2, 1))
println(PriorityPair("A", "B"))

data class Task(val id: Int, val name: String, val completed: Boolean, val assigned: Boolean)

val task1 = Task(1, "Create project", completed = false, assigned = true)
println(task1)
val task1Compleded = task1.copy(completed = true, assigned = false)
println(task1Compleded)

interface Remote {
    fun up()
    fun down()

    fun doubleUp() {
        up()
        up()
    }


    companion object {
        fun combine(first: Remote, second: Remote) = object: Remote {
            override fun up() {
                first.up()
                second.up()
            }

            override fun down() {
                first.down()
                second.down()
            }
        }
    }
}

class TV {
    private var volume = 0

    val remote: Remote get() = object: Remote {
        override fun up() {
            volume++
        }

        override fun down() {
            volume--
        }

        override fun toString(): String {
            return "TVRemote(${this@TV.toString()})"
        }
    }

    override fun toString() = "TV(volume=$volume)"
}

val tv = TV()
val remote = tv.remote
println(tv)
remote.up()
println(tv)
remote.doubleUp()
println(tv)

open class Vehicle(val year: Int, open var color: String) {
    open val km = 0

    final override fun toString() = "Vehicle(year=$year, color='$color', km=$km)"

    fun repaint(newColor: String) {
        color = newColor
    }
}

open class RegularCar(year: Int, color: String) : Vehicle(year, color) {
    override var km: Int = 0
        set(value) {
            if (value < 1) {
                throw IllegalArgumentException("negative value")
            }
            field = value
        }

    fun drive(distance: Int) {
        km += distance
    }
}

class FamilyCar(year: Int, color: String) : RegularCar(year, color) {
    override var color: String
        get() = super.color
        set(value) {
            if (value.isEmpty()) {
                throw IllegalArgumentException("Color required")
            }
            super.color = value
        }
}
