package me.alexkovrigin.splitthebill.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import me.alexkovrigin.splitthebill.services.api.ReceiptInfo

@Entity(tableName = "LoadedReceipts")
open class Receipt(
    @PrimaryKey
    open val qr: String,
    open val dateTime: String,
    open val _retailPlace: String?
) {
    constructor(qr: String, receiptInfo: ReceiptInfo) : this(
        qr,
        receiptInfo.dateTime,
        receiptInfo._retailPlace
    )

    val displayRetailPlace: String
        get() = _retailPlace ?: "Unknown"
}

class ReceiptWithItems(
    override val qr: String,
    override val dateTime: String,
    override val _retailPlace: String?,
    @Relation(parentColumn = "qr", entityColumn = "qr")
    val items: List<Item>
) : Receipt(
    qr,
    dateTime,
    _retailPlace
)