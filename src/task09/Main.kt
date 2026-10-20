package task09

@PublishedApi
internal var debugEnabled = false

inline fun debug(message: () -> String) {
    if (debugEnabled) println("[DEBUG] ${message()}")
}

fun buildReport(): String {
    println("(building the report...)")
    return "report with 1000 rows"
}

fun main() {
    print("Enable debug logging? (yes/no): ")
    debugEnabled = readln().trim().equals("yes", ignoreCase = true)
    debug { buildReport() }
    println("Program finished")
}
