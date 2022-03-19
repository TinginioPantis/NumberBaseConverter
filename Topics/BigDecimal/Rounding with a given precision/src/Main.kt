import java.math.BigDecimal
import java.math.RoundingMode

fun main() {             
    val input = readLine()!!.toBigDecimal()
    val newScale = readLine()!!.toInt()
    val result = input.setScale(newScale, RoundingMode.HALF_DOWN)
    println(result)
}