package me.alexkovrigin.splitthebill.utilities

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.ui.theme.usersPalette

object PaletteUtils {
    val colorSaver = Saver<Color, String>(
        save = {
            it.value.toString()
        },
        restore = {
            Color(value = it.toULong())
        }
    )

    fun pickColorForUser(user: User): Color {
        val hashCode = user.hashCode().toLong()
        val randomIndex = ((hashCode * 17) % usersPalette.size).toInt()
        return usersPalette[randomIndex]
    }
}