import java.util.*

fun main() {
    val sizeOfList = readLine()!!.toInt()
    val mutList: MutableList<Int> = mutableListOf()
    for (sizeOfList in 0 until sizeOfList) {
        mutList.add(readLine()!!.toInt())
    }
    val rotationNumber = readLine()!!.toInt() % sizeOfList
    Collections.rotate(mutList, rotationNumber)
    println(mutList.joinToString(" "))
}