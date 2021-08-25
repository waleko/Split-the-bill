package me.alexkovrigin.splitthebill.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.Receipt
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.utilities.SingletonHolder

@Database(
    entities = [
        User::class,
        Receipt::class,
        Item::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class BillDatabase : RoomDatabase() {

    companion object : SingletonHolder<BillDatabase, Context>({
        Room.databaseBuilder(
            it.applicationContext,
            BillDatabase::class.java,
            "splitthebill.db"
        ).build()
    })

    abstract fun dao(): BillDao
}