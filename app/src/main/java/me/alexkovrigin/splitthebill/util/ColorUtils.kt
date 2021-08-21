package me.alexkovrigin.splitthebill.util

import androidx.compose.ui.graphics.Color

object PaletteUtils {
    val palette: List<Color> = listOf(
        Color.Red,
        Color.Blue,
        Color.Green
    )

    fun pickColorForUser(userIndex: Int): Color {
        return palette[userIndex % palette.size]
    }

    private val logTag = "PalleteUtils"
}