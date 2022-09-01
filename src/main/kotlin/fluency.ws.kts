import kotlin.math.abs

data class Complex(val real: Double, val imaginary: Double) {
    operator fun times(other: Complex) =
        Complex(real * other.real - imaginary * other.imaginary,
                real * other.imaginary + imaginary * other.real)

    private fun sign() = if (imaginary < 0) "-" else "+"

    override fun toString() = "$real ${sign()} ${abs(imaginary)}i"
}

println(Complex(4.0, 2.0) * Complex(-3.0, 4.0))
println(Complex(1.4, 2.2) * Complex(-3.8, 4.2))

data class Point(val x: Int, val y: Int)
data class Circle(val cx: Int, val cy: Int, val radius: Int)

operator fun Circle.contains(point: Point) =
    (point.x - cx) * (point.x - cx) + (point.y - cy) * (point.y - cy) < radius * radius

val circle = Circle(cx = 100, cy = 100, radius = 25)
val point1 = Point(110, 110)
val point2 = Point(10, 100)
println(circle.contains(point1))
println(circle.contains(point2))
println(point1 in circle)
println(point2 in circle)

val Circle.area: Double
    get() = kotlin.math.PI * radius * radius

println("Areas is ${circle.area}")

fun String.isPalindrome(): Boolean = reversed() == this
fun String.shout() = toUpperCase()

val str = "dad"
println(str.isPalindrome())
println(str.shout())
