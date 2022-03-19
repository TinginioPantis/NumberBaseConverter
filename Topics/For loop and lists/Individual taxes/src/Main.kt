fun main() {
    val n = readLine()!!.toInt()
    val incomes = MutableList<Double>(n) { readLine()!!.toDouble() }
    val taxRates = MutableList<Double>(n) { readLine()!!.toDouble() / 100 }
    val taxPaid = incomes.zip(taxRates) { a: Double, b: Double -> a * b }
    val maxTaxPaid = taxPaid.indexOf(taxPaid.maxOrNull()) + 1
    println(maxTaxPaid)
}