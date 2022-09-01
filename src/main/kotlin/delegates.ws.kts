import java.lang.AssertionError

interface Worker {
    fun work() // work is not so fun all the time...
    fun takeVacation() // that's more fun
    fun fillTimeSheet() = println("Why? Really?")
}

class JavaProgrammer: Worker {
    override fun work() = println("write Java")
    override fun takeVacation() = println("Code at the beach")
}

class CSharpProgrammer: Worker {
    override fun work() = println("write C#")
    override fun takeVacation() = println("Branch at the ranch")
}

interface Assistant {
    fun doChores() // definitely not fun!
    fun fillTimeSheet() = println("No escape from that!")
}

class DepartmentAssistant: Assistant {
    override fun doChores() = println("routine stuff")
}

class Manager(val staff: Worker, val assistant: Assistant):
    Worker by staff, Assistant by assistant {
    fun meeting() =
        println("organizing meeting with ${staff.javaClass.simpleName}")

    override fun takeVacation() = println("of course")

    override fun fillTimeSheet() = assistant.fillTimeSheet()
}

val doe = Manager(CSharpProgrammer(), DepartmentAssistant())
val roe = Manager(JavaProgrammer(), DepartmentAssistant())
doe.work()
doe.meeting()
doe.takeVacation()
doe.doChores()
doe.fillTimeSheet()
roe.work()
roe.meeting()
