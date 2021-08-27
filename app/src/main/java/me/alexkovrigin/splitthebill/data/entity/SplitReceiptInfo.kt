package me.alexkovrigin.splitthebill.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity for a split receipt. Stores only its [uid] and [qr] code that corresponds to receipt.
 *
 * @see [me.alexkovrigin.splitthebill.data.dataviews.SplitReceipt]
 * @see [me.alexkovrigin.splitthebill.data.dataviews.SplitReceiptWithItemsAndSplitting]
 */
@Entity(
    tableName = "SplitReceiptInfos",
    foreignKeys = [
        ForeignKey(
            entity = Receipt::class,
            parentColumns = ["qr"],
            childColumns = ["qr"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["qr"])
    ]
)
open class SplitReceiptInfo(
    @PrimaryKey
    open val uid: String,
    open val qr: String
)

/**
 * Creates a new [SplitReceiptInfo] for [Receipt] by generating a new uuid and getting the [Receipt.qr]
 */
fun Receipt.newSplitReceiptInfo(): SplitReceiptInfo = SplitReceiptInfo(
    uid = UUID.randomUUID().toString(),
    qr = qr
)