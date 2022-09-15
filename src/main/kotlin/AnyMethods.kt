class AnyMethods {
    private val format = "%-10s%-10s%-10s%-10s"
    private val str = "context"
    private val result = "RESULT"

    override fun toString() = "lexical"

    fun main() {
        println(
            String.format(
                "%-10s%-10s%-10s%-10s%-10s",
                "Method", "Argument", "Receiver", "Return", "Result"
            )
        )
        println("===============================================")

//        public inline fun <T, R> T.let(block: (T) -> R): R {
//            return block(this)
//        }
        val result1 = str.let { arg ->
            print(String.format(format, "let", arg, this, result))
            result
        }
        println(String.format("%-10s", result1))

//        public inline fun <T> T.also(block: (T) -> Unit): T {
//            block(this)
//            return this
//        }
        val result2 = str.also { arg ->
            print(String.format(format, "also", arg, this, result))
            result
        }
        println(String.format("%-10s", result2))

//        public inline fun <T, R> T.run(block: T.() -> R): R {
//            return block()
//        }
        val result3 = str.run {
            print(String.format(format, "run", "N/A", this, result))
            result
        }
        println(String.format("%-10s", result3))

//        public inline fun <T> T.apply(block: T.() -> Unit): T {
//            block()
//            return this
//        }
        val result4 = str.apply {
            print(String.format(format, "apply", "N/A", this, result))
            result
        }
        println(String.format("%-10s", result4))
    }
}

class Mailer {
    val details = StringBuilder()
    fun from(addr: String) = details.append("from $addr...\n")
    fun to(addr: String) = details.append("to $addr...\n")
    fun subject(line: String) = details.append("subject $line...\n")
    fun body(message: String) = details.append("body $message...\n")
    fun send() = "...sending...\n$details"
}

fun verbose() {
    val mailer = Mailer()
    mailer.from("builder@agiledeveloper.com")
    mailer.to("venkats@agiledeveloper.com")
    mailer.subject("Your code sucks")
    mailer.body("...details...")
    val result = mailer.send()
    println(result)
}

fun useApply1() {
    val mailer =
        Mailer()
            .apply { from("builder@agiledeveloper.com") }
            .apply { to("venkats@agiledeveloper.com") }
            .apply { subject("Your code sucks") }
            .apply { body("details") }
    val result = mailer.send()
    println(result)
}

fun useApply2() {
    val mailer = Mailer().apply {
        from("builder@agiledeveloper.com")
        to("venkats@agiledeveloper.com")
        subject("Your code sucks")
        body("details")
    }
    val result = mailer.send()
    println(result)
}

fun useRun() {
    val result = Mailer().run {
        from("builder@agiledeveloper.com")
        to("venkats@agiledeveloper.com")
        subject("Your code sucks")
        body("details")
        send()
    }
    println(result)
}

fun createMailer() = Mailer()

fun prepareAndSend(mailer: Mailer) = mailer.run {
    from("builder@agiledeveloper.com")
    to("venkats@agiledeveloper.com")
    subject("Your code suks")
    body("details")
    send()
  }

fun useLet1() {
    val mailer = createMailer()
    val result = prepareAndSend(mailer)
    println(result)
}

fun useLet2() {
    val result = prepareAndSend(createMailer())
    println(result)
}

fun useLet3() {
    val result = createMailer().let { mailer ->
      prepareAndSend(mailer)
    }
    println(result)
}

fun useLet4() {
    val result = createMailer().let {
      prepareAndSend(it)
    }
    println(result)
}

fun useLet5() {
    val result = createMailer().let(::prepareAndSend)
    println(result)
}

fun main() {
    AnyMethods().main()
}

fun prepareMailer(mailer: Mailer):Unit {
  mailer.run {
    from("builder@agiledeveloper.com")
    to("venkats@agiledeveloper.com")
    subject("Your code suks")
    body("details")
  }
}

fun sendMail(mailer: Mailer): Unit {
  mailer.send()
  println("Mail sent")
}

fun useAlso() {
    val mailer = createMailer()
    prepareMailer(mailer)
    sendMail(mailer)

    createMailer()
        .also(::prepareMailer)
        .also(::sendMail)
}

