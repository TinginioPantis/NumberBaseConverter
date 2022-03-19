fun main() {
    val n = readLine()!!.toInt()
    val list = MutableList<Int>(n) { readLine()!!.toInt() }
    val listSorted = list.sorted()
    if (list == listSorted) {
        println("YES")
    } else {
        println("NO")
    }
}