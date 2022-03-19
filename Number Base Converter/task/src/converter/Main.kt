package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

const val BaseLetters = "0123456789abcdefghijklmnopqrstuvwxyz"
val mc: MathContext = MathContext.DECIMAL128

fun main() {
    firstLevelMenu()
}

sealed interface FirstLevelMenuCommand
data class FirstLevelMenuConvert(val sourceBase: Int, val targetBase: Int) : FirstLevelMenuCommand
object FirstLevelMenuExit : FirstLevelMenuCommand

sealed interface SecondLevelMenuCommand
data class SecondLevelMenuConvert(val sourceNumber: String) : SecondLevelMenuCommand
object SecondLevelMenuExit : SecondLevelMenuCommand

fun firstLevelMenu() {
    var userMove = firstLevelMenuAsk()
    while (userMove != FirstLevelMenuExit) {
        when (userMove) {
            is FirstLevelMenuConvert -> secondLevelMenu(userMove)
            FirstLevelMenuExit -> throw Exception("Impossible!")
        }
        println()
        //paklausk vel klausimo ka daryt, priskirti atsakyma i usermove
        userMove = firstLevelMenuAsk()
    }
}

fun secondLevelMenu(bases: FirstLevelMenuConvert) {
    var userMove = secondLevelMenuAsk(bases)
    while (userMove != SecondLevelMenuExit) {
        when (userMove) {
            is SecondLevelMenuConvert -> {
                try {
                    val sourceNumber = userMove.sourceNumber
                    val result = sourceToTargetConversion(
                            bases.sourceBase, bases.targetBase, sourceNumber = sourceNumber,
                            targetFractionLength = 5
                    )
                    val resultStr = result ?: "Can't convert '$sourceNumber' from base ${bases.sourceBase}!"
                    println("Conversion result: $resultStr")
                }
                catch (e: NumberFormatException) {
                    println("Can't parse '${userMove.sourceNumber}' as base ${bases.sourceBase}!")
                }
            }
            SecondLevelMenuExit -> throw Exception("Impossible!")
        }
        println()
        //paklausk vel klausimo ka daryt, priskirti atsakyma i usermove
        userMove = secondLevelMenuAsk(bases)
    }
}

fun firstLevelMenuAsk(): FirstLevelMenuCommand {
    println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
    val input = readLine()!!
    when (input) {
        "/exit" -> {
            return FirstLevelMenuExit
        }
        else -> {
            try {
                val inputSplitted = input.split(" ")
                val sourceBase = inputSplitted[0].toInt()
                val targetBase = inputSplitted[1].toInt()
                return FirstLevelMenuConvert(sourceBase = sourceBase, targetBase = targetBase)
            }
            catch (e: Exception) {
                println("Unknown command...")
                return firstLevelMenuAsk()
            }
        }
    }
}

fun secondLevelMenuAsk(bases: FirstLevelMenuConvert): SecondLevelMenuCommand {
    println("Enter number in base ${bases.sourceBase} to convert to base ${bases.targetBase} (To go back type /back)")
    val input = readLine()!!
    when (input) {
        "/back" -> {
            return SecondLevelMenuExit
        }
        else -> {
            return SecondLevelMenuConvert(sourceNumber = input)
        }
    }
}

fun sourceToTargetConversion(
        sourceBase: Int, targetBase: Int, sourceNumber: String, targetFractionLength: Int
): String? {
    val sourceToDecimalResult =
        if (sourceBase == 10) StrToDecimalBaseResult(
                sourceNumber.toBigDecimal(mc),
                hadFractionalPart = sourceNumber.contains('.')
        )
        else strToDecimalBase(sourceNumber, sourceBase)
//    println("as decimal: $sourceToDecimalResult")
    if (sourceToDecimalResult != null) {
        val decimalToTargetResult = decimalBaseToTargetBase(
                number = sourceToDecimalResult.number, targetBase.toBigInteger(),
                targetFractionLength = if (sourceToDecimalResult.hadFractionalPart) targetFractionLength else null
        )
        return decimalToTargetResult
    }
    else return null
}

fun BigDecimal.isZero(): Boolean = this.compareTo(BigDecimal.ZERO) == 0

/**
 * Takes a number with an integer and fractional parts in decimal base and converts to target base. */
fun decimalBaseToTargetBase(
        number: BigDecimal, targetBase: BigInteger, targetFractionLength: Int?
): String {
    val split = number.toString().split(".", limit = 2)
    val integerPart = BigInteger(split[0])
    val fractionalPart = if (split.size == 2) BigDecimal("0.${split[1]}") else BigDecimal.ZERO
    val convertedIntegerPart =  decimalBaseToTargetBaseInteger(number = integerPart, targetBase)
//    println("b10 -> b$targetBase: integer=$convertedIntegerPart")
    val convertedFractionalPart =
            if (targetFractionLength == null) null
            else decimalBaseToTargetBaseFractional(
                    number = fractionalPart, targetBase, length = targetFractionLength
            )
//    println("b10 -> b$targetBase: fractional=$convertedFractionalPart")
    val result =
            if (convertedFractionalPart == null) convertedIntegerPart
            else "$convertedIntegerPart.$convertedFractionalPart"
//    println("b10 -> b$targetBase: result=$result")
    return result
}

/** Takes a fractional part in decimal base and converts to target base. */
fun decimalBaseToTargetBaseFractional(number: BigDecimal, targetBase: BigInteger, length: Int): String {
    if (number >= BigDecimal.ONE || number < BigDecimal.ZERO)
        throw IllegalArgumentException("number ($number) should be in range 0 <= number < 1!")

    val integerPartsList = mutableListOf<Int>()
    val targetBaseDecimal = targetBase.toBigDecimal(mathContext = mc)
//    fraction dali padauginti is targetbazes, gauto skaiciaus integer part isitraukt i sarasa
//    gauto skaiciaus fraction dali vel dauginti is target bazes, iki kol gautos reiksmes fractional dalis = 0
    var currentNumber = number
    do {
//        println("b10 -> b$targetBase fractional: currentNumber=$currentNumber")
        val multiplied = currentNumber * targetBaseDecimal
//        println("b10 -> b$targetBase fractional: multiplied=$multiplied")
        integerPartsList.add(multiplied.toInt())
        currentNumber = multiplied - multiplied.toBigInteger().toBigDecimal()
    } while (!currentNumber.isZero() && integerPartsList.size < length)

//    println("b10 -> b$targetBase fractional: integerPartsList=$integerPartsList")
    var result = integerPartsList.map { intToChar(it) }.joinToString("")
    if (result.length < length) result += "0".repeat(length - result.length)
//    println("b10 -> b$targetBase fractional: result=$result")
    return result
}

/** Takes an integer (non-fractional) part in decimal base and converts to target base. */
fun decimalBaseToTargetBaseInteger(number: BigInteger, targetBase: BigInteger): String {
    val remaindersList = mutableListOf<Int>()
    var quotient = number
    // vykdyti iki kol quotient bus lygus 0
    do {
        val remainder = (quotient % targetBase).toInt()
        remaindersList.add(remainder)
        quotient /= targetBase
    } while (quotient != BigInteger.ZERO)
    //remainder pridedami i remaindersList
    //galutinis rezultatas reversed remaindersList
    remaindersList.reverse()
    return remaindersList.map { intToChar(it) }.joinToString("")
}

data class StrToDecimalBaseResult(val number: BigDecimal, val hadFractionalPart: Boolean)
/**
 * Takes a string, splits into 2 parts: before and after '.'
 * Converts 1st part, converts 2nd part.
 * Adds 2 parts together with '.' in between and converts toBigDecimal.
 */
fun strToDecimalBase(sourceNumber: String, sourceBase: Int): StrToDecimalBaseResult? {
    val split = sourceNumber.split(".", limit = 2)
    val integerPart = split[0]
    val hasFractionalPart = split.size == 2
    val fractionalPart = if (hasFractionalPart) split[1] else "0"
    val convertedIntegerPart = strToDecimalBasePart(sourceNumber = integerPart, sourceBase, fractional = false)
    val convertedFractionalPart = strToDecimalBasePart(sourceNumber = fractionalPart, sourceBase, fractional = true)
//    println("int=$convertedIntegerPart, frac=$convertedFractionalPart")
    if (convertedIntegerPart != null && convertedFractionalPart != null) {
        val result = convertedIntegerPart + convertedFractionalPart
        return StrToDecimalBaseResult(result, hasFractionalPart)
    }
    else {
        return null
    }
}

/**
 * Parses a [sourceNumber] without '.' into the given [sourceBase].
 *
 * If [fractional] is true, then the [sourceNumber] should be the part to the right of the '.',
 * for example '1001' in '10000.1001', while if it is false, it should be the part to the left of
 * the '.', for example '10000' in the previous example.
 *
 * Returns null if parsing failed.
 **/
fun strToDecimalBasePart(sourceNumber: String, sourceBase: Int, fractional: Boolean): BigDecimal? {
    fun processChar(char: Char, idx: Int): BigDecimal? {
        //imti kiekviena chara ir padauginti is sourceBase pakelto tuo laipsniu,
        // koks charo indeksas
        val charAsInt = charToInt(char)
        // Check if the looked up character fits into given `sourceBase`.
        if (charAsInt == null || charAsInt >= sourceBase) {
            return null
        } else {
            val power = if (fractional) -(idx+1) else idx
//            println("char=$char, charAsInt=$charAsInt, idx=$idx, base=$sourceBase, power=$power")
            val multiplier = sourceBase.toBigDecimal(mc).pow(power, mc)
//            println("char=$char, charAsInt=$charAsInt, idx=$idx, base=$sourceBase, multiplier=$multiplier")
            return charAsInt.toBigDecimal(mc) * multiplier
        }
    }

    //pareversinti stringa ir sudalinti i charus
    val zipped = (if (fractional) sourceNumber else sourceNumber.reversed()).toCharArray().zip(sourceNumber.indices) { char, idx ->
        processChar(char, idx)
    }
    return if (zipped.contains(null)) null else zipped.fold(BigDecimal.ZERO) { a, b -> a + b!! }
}

fun charToInt(c: Char): Int? = when (val i = BaseLetters.indexOf(c.lowercaseChar())) {
    -1 -> null
    else -> i
}

fun intToChar(value: Int): Char {
    //suindeksuoti pagal value i basseletters
    return BaseLetters[value]
}