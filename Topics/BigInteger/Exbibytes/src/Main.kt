import java.math.BigInteger

fun main() {
    val exbibyte = readLine()!!.toBigInteger()
    val two = BigInteger.TWO
    val conversion = exbibyte * two.pow(63)
    println(conversion)
}