package me.alexkovrigin.splitthebill.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import me.alexkovrigin.splitthebill.services.api.ReceiptInfo

@Entity(tableName = "LoadedReceipts")
@Parcelize
open class Receipt(
    @PrimaryKey
    open val qr: String,
    open val dateTime: String,
    open val _retailPlace: String?
) : Parcelable {
    constructor(qr: String, receiptInfo: ReceiptInfo) : this(
        qr,
        receiptInfo.dateTime,
        receiptInfo._retailPlace
    )

    val displayRetailPlace: String
        get() = _retailPlace ?: "Unknown"
}

@Parcelize
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
) {
    fun format(): ReceiptWithItems {
        val receiptsMap = mutableMapOf<String, Item>()

        items.forEach {
            receiptsMap.merge(it.name, it) { a, b ->
                a + b
            }
        }

        val newItems = receiptsMap.values.toList()

        return ReceiptWithItems(
            qr = qr,
            dateTime = dateTime,
            _retailPlace = _retailPlace,
            items = newItems
        )
    }
}