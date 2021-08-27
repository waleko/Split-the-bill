package me.alexkovrigin.splitthebill.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Entity that stores the receipt splitting as a [SplittingMap] (basically a 2-dimensional array).
 *
 * It is saved in the database using [me.alexkovrigin.splitthebill.data.Converters]
 */
@Entity(
    tableName = "Splittings",
    foreignKeys = [
        ForeignKey(
            entity = SplitReceiptInfo::class,
            parentColumns = ["uid"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["uid", "value"]
)
class Splitting(
    val uid: String,
    val value: SplittingMap
)

typealias SplittingMap = Map<User, List<Int?>>