package me.alexkovrigin.splitthebill.utilities

import me.alexkovrigin.splitthebill.EPS
import kotlin.math.abs
import kotlin.math.roundToInt

fun Int.asRubles(): String {
    val cents = this % 100
    return if (cents == 0)
        (this / 100).toString()
    else
        "%.2f".format(this.toDouble() / 100)
}

private fun Double.canBeConsideredInt(eps: Double = EPS) = abs(roundToInt() - this) < eps

fun Double.asDisplay(eps: Double = EPS): String {
    return if (canBeConsideredInt(eps))
        roundToInt().toString()
    else
        toString()
}