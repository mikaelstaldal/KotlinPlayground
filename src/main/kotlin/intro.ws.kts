import java.lang.Appendable
import java.lang.StringBuilder

val names = listOf("Tom", "Jerry", "Spike")

for ((index, name) in names.withIndex()) {
    println("Position of $name is $index")
}

fun whatToDo(dayOfWeek: Any) = when (dayOfWeek) {
    "Saturday", "Sunday" -> "Relax"
    in listOf("Monday", "Tuesday", "Wednesday", "Thursday") -> "Work Harder"
    in 2..4 -> "Work hard"
    "Friday" -> "Party"
    is String -> "What, you provided a string of length ${dayOfWeek.length}"
    else -> "No clue"
}

whatToDo("Sunday")
whatToDo("Foo")
whatToDo(3)
whatToDo(17)

fun systemInfo() = when (val cores = Runtime.getRuntime().availableProcessors()) {
    1 -> "1 core, old and slow!"
    in 2..16 -> "decent machine with $cores cores"
    else -> "$cores cores! I want your machine!"
}

systemInfo()

Array(5) { i -> (i + 1) * (i + 1) }.contentToString()

fun nickName(name: String?) = when (name) {
    "William" -> "Bill"
    null -> "Joker"
    else -> name.reversed().uppercase()
}

nickName("William")
nickName("Mikael")
nickName(null)

fun fetchMessage(id: Int): Any =
    if (id == 1) "Record found" else StringBuilder("data not found")

for (id in 1..2) {
    println("Message length: ${(fetchMessage(id) as? String)?.length ?: "---"}")
}

fun <T> useAndClose(input: T)
        where T : Appendable,
              T : AutoCloseable {
    input.append("there")
    input.close()
}

val writer = java.io.StringWriter()
writer.append("Hello ")
useAndClose(writer)
println(writer)

fun printValues(values: Array<*>) {
    for (value in values) {
        println(value)
    }
}

printValues(arrayOf(1, 2, 3))

abstract class Book(val name: String)
class Fiction(name: String) : Book(name)
class NonFiction(name: String) : Book(name)

inline fun <reified T> findFirst(books: List<Book>): T =
    books.first { book -> book is T } as T

val books = listOf(Fiction("Moby dick"), NonFiction("Learn to code"), Fiction("Lord of the Rings"))

println(findFirst<NonFiction>(books).name)
