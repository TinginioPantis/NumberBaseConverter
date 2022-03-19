fun main() {
    val n = readLine()!!.toInt()
    val list = MutableList<Int>(n) { readLine()!!.toInt() }
    val twoNumbers = readLine()!!.split(" ")
    val p = twoNumbers[0].toInt()
    val m = twoNumbers[1].toInt()
    if (list.contains(p) && list.contains(m)) {
        println("YES")
    } else println("NO")
}