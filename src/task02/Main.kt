package task02

val pending = mutableListOf<() -> Unit>()

inline fun addTask(save: () -> Unit, noinline reminder: () -> Unit) {
    save()
    pending.add(reminder)
}

fun main() {
    print("Task name: ")
    val name = readln().trim()
    addTask(
        save = { println("Saved: $name") },
        reminder = { println("Reminder: finish '$name'") },
    )
    println("Pending reminders: ${pending.size}")
    pending.forEach { it() }
}
