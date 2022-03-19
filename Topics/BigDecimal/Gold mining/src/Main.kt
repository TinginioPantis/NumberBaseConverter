import java.math.BigDecimal

fun main() {
    val dwalin = BigDecimal(readLine()!!)
    val balin = BigDecimal(readLine()!!)
    val thorin = BigDecimal(readLine()!!)
    val totalGold = dwalin + balin + thorin
    println(totalGold)
}