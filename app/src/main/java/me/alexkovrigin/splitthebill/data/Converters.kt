package me.alexkovrigin.splitthebill.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.alexkovrigin.splitthebill.data.entity.SplittingMap
import me.alexkovrigin.splitthebill.utilities.fromJson

/**
 * [BillDatabase] converters
 */
class Converters {
    companion object {
        @JvmStatic
        private val gson: Gson = GsonBuilder()
            .enableComplexMapKeySerialization()
            .create()
    }

    @TypeConverter
    fun splittingFromJson(value: String): SplittingMap = gson.fromJson(value)

    @TypeConverter
    fun splittingToJson(splitting: SplittingMap): String = gson.toJson(splitting)
}