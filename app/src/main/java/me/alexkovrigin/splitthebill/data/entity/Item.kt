package me.alexkovrigin.splitthebill.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import me.alexkovrigin.splitthebill.services.api.ItemInfo
import me.alexkovrigin.splitthebill.util.asRubles

@Entity(
    tableName = "Items",
    foreignKeys = [
        ForeignKey(
            entity = Receipt::class,
            parentColumns = ["qr"],
            childColumns = ["qr"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["qr"])],
    primaryKeys = ["qr", "name", "positionInReceipt"]
)
class Item(
    val qr: String,
    val name: String,
    val _raw_priceForSingle: Int,
    val _raw_sum: Int,
    val quantity: Double,
    val positionInReceipt: Int
) {
    constructor(qr: String, itemInfo: ItemInfo, positionInReceipt: Int) : this(
        qr,
        itemInfo.name,
        itemInfo._raw_priceForSingle,
        itemInfo._raw_sum,
        itemInfo.quantity,
        positionInReceipt
    )

    val displayPriceForSingle: String
        get() = _raw_priceForSingle.asRubles()
    val displaySum: String
        get() = _raw_sum.asRubles()
}

