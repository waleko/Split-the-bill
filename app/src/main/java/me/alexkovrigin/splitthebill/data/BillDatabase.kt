package me.alexkovrigin.splitthebill.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.alexkovrigin.splitthebill.data.dataviews.ReceiptWithItems
import me.alexkovrigin.splitthebill.data.dataviews.SplitReceipt
import me.alexkovrigin.splitthebill.data.dataviews.SplitReceiptWithItemsAndSplitting
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.Receipt
import me.alexkovrigin.splitthebill.data.entity.SplitReceiptInfo
import me.alexkovrigin.splitthebill.data.entity.Splitting
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.utilities.SingletonHolder

@Database(
    entities = [
        User::class,
        Receipt::class,
        Item::class,
        SplitReceiptInfo::class,
        Splitting::class
    ],
    views = [
        ReceiptWithItems::class,
        SplitReceipt::class,
        SplitReceiptWithItemsAndSplitting::class,
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(Converters::class)
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