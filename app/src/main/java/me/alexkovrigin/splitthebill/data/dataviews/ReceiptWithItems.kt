package me.alexkovrigin.splitthebill.data.dataviews

import androidx.room.DatabaseView
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.Receipt

/**
 * [DatabaseView] expanding [Receipt] with items associated with the receipt.
 */
@Parcelize
@DatabaseView("SELECT * FROM LoadedReceipts LR")
class ReceiptWithItems(
    override val qr: String,
    override val dateTime: String,
    override val _retailPlace: String?,
    @Relation(parentColumn = "qr", entityColumn = "qr")
    val items: List<Item>
) : Receipt(
    qr = qr,
    dateTime = dateTime,
    _retailPlace = _retailPlace
) {
    // TODO: 27.08.2021 move to Item
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