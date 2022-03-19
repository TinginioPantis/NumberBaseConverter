import java.util.*

fun main() {
    val a = readLine()!!.toInt()
    val list = MutableList<Int>(a) { readLine()!!.toInt() }
    Collections.rotate(list, 1)
    println(list.joinToString(" "))
}