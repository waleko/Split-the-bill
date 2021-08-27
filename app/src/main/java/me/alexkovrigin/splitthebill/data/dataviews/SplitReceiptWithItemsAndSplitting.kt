package me.alexkovrigin.splitthebill.data.dataviews

import androidx.room.DatabaseView
import androidx.room.Relation
import me.alexkovrigin.splitthebill.data.entity.SplitReceiptInfo
import me.alexkovrigin.splitthebill.data.entity.Splitting
import me.alexkovrigin.splitthebill.data.entity.SplittingMap

/**
 * [DatabaseView] extending [SplitReceiptInfo] with [ReceiptWithItems] and [Splitting].
 *
 * Used for primary summary page, as it contains all necessary fields.
 */
@DatabaseView("SELECT SRI.* FROM SplitReceiptInfos SRI")
class SplitReceiptWithItemsAndSplitting(
    override val uid: String,
    override val qr: String,
    @Relation(parentColumn = "qr", entityColumn = "qr")
    val receiptWithItems: ReceiptWithItems,
    @Relation(parentColumn = "uid", entityColumn = "uid")
    private val _splitting: Splitting
) : SplitReceipt(
    uid = uid,
    qr = qr,
    receipt = receiptWithItems
) {
    val splitting: SplittingMap
        get() = _splitting.value
}