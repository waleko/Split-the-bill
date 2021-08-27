package me.alexkovrigin.splitthebill.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import me.alexkovrigin.splitthebill.services.api.ReceiptInfo
import java.time.Instant
import java.util.Date

/**
 * Entity representing a receipt loaded from FNS server.
 *
 * @see me.alexkovrigin.splitthebill.data.dataviews.ReceiptWithItems
 */
@Entity(tableName = "LoadedReceipts")
@Parcelize
open class Receipt(
    /*
    TODO: same receipt may have multiple correct qr code representations,
     it would be nice to store them as one receipt, for example by making ticketId
     primary key. This is not severe though.
    */
    /**
     * Qr code that was used to load the receipt.
     */
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

    val date: Date
        get() = Date.from(Instant.ofEpochSecond(dateTime.toLong()))
}
