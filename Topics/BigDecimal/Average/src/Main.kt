import java.math.BigDecimal
import java.math.RoundingMode
const val THREE = 3

fun main() {
    val a = BigDecimal(readLine()!!)
    val b = BigDecimal(readLine()!!)
    val c = BigDecimal(readLine()!!)
    val result = ((a + b + c) / BigDecimal(THREE)).setScale(0, RoundingMode.DOWN)
    println(result)
}