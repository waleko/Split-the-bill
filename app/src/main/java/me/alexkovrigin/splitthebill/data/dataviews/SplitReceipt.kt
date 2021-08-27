package me.alexkovrigin.splitthebill.data.dataviews

import androidx.room.DatabaseView
import androidx.room.Relation
import me.alexkovrigin.splitthebill.data.entity.Receipt
import me.alexkovrigin.splitthebill.data.entity.SplitReceiptInfo

/**
 * [DatabaseView] extending [SplitReceiptInfo] with [Receipt].
 *
 * Used for homepage list, as it is lightweight,
 * because it doesn't load neither items nor splitting.
 */
@DatabaseView("SELECT SRI.* FROM SplitReceiptInfos SRI")
open class SplitReceipt(
    override val uid: String,
    override val qr: String,
    @Relation(parentColumn = "qr", entityColumn = "qr")
    open var receipt: Receipt
) : SplitReceiptInfo(
    uid = uid,
    qr = qr
)