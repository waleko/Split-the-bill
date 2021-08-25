package me.alexkovrigin.splitthebill.utilities

fun Int.asRubles() = "%.2f".format(this.toDouble() / 100)
