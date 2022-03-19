fun main() {
    val input = readLine()!!
    val splitted = input.split("-")
    val year = splitted[0]
    val month = splitted[1]
    val day = splitted[2]
    println("$month/$day/$year")
}