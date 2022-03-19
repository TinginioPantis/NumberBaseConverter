import java.math.BigInteger

fun main() {
    val a = readLine()!!.toBigInteger()
    val b = readLine()!!.toBigInteger()
    val sum = a + b
    val hundred = BigInteger.valueOf(100)
    val aPercentage = a * hundred / sum
    val bPercentage = b * hundred / sum
    println("$aPercentage% $bPercentage%")
}