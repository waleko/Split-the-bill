package me.alexkovrigin.splitthebill.util

import androidx.compose.ui.graphics.Color
import me.alexkovrigin.splitthebill.data.entity.User

object PaletteUtils {
    val palette: List<Color> = listOf(
        Color.Red,
        Color.Blue,
        Color.Green
    )

    fun pickColorForUser(user: User, userIndex: Int): Color {
        return palette[userIndex % palette.size]
    }

    private val logTag = "PalleteUtils"
}