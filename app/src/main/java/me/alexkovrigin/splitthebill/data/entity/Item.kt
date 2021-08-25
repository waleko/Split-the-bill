package me.alexkovrigin.splitthebill.data.entity

import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.parcelize.Parcelize
import me.alexkovrigin.splitthebill.services.api.ItemInfo
import me.alexkovrigin.splitthebill.utilities.asRubles
import kotlin.math.min

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
@Parcelize
class Item(
    /**
     * Receipt's qr code, that this item belongs to
     */
    val qr: String,
    /**
     * Name of the item in the receipt. Used to identify the item.
     */
    val name: String,
    /**
     * Price for an item with quantity equal to 1.0. For example, one piece, one kg, one liter, etc.
     *
     * Warning: This value may be invalid, i.e. not equal to `sum` / `quantity`.
     * This is due to [plus] operator. See [issue 1](https://github.com/waleko/Split-the-bill/issues/1) for details
     */
    @Deprecated(
        "Please only use this value  for displaying. " +
            "Price for single may be not equal to `sum` / `quantity`. " +
            "Learn more: https://github.com/waleko/Split-the-bill/issues/1"
    )
    val _raw_priceForSingle: Int,
    /**
     * Item sum for [quantity] pieces.
     *
     * For calculations please use this and not the [_raw_priceForSingle].
     *
     * It is guaranteed that the item sum correctly represents the sum of children items' `priceForSingle` multiplied by [quantity].
     */
    val _raw_sum: Int,
    /**
     * Amount of pieces in an item. May be non-integer, for example if buying 2.4 kg of carrots,
     * the quantity will be 2.4
     */
    val quantity: Double,
    val positionInReceipt: Int
) : Parcelable {
    constructor(qr: String, itemInfo: ItemInfo, positionInReceipt: Int) : this(
        qr,
        itemInfo.name,
        itemInfo._raw_priceForSingle,
        itemInfo._raw_sum,
        itemInfo.quantity,
        positionInReceipt
    )

    val displayPriceForSingle: String
        @Suppress("DEPRECATION")
        get() = _raw_priceForSingle.asRubles()
    val displaySum: String
        get() = _raw_sum.asRubles()

    /**
     * Merges two items
     */
    operator fun plus(other: Item): Item {
        require(this.qr == other.qr) {"Cannot add items from different receipts"}
        val newName = if (this.name == other.name) {
            this.name
        } else {
            Log.e("Item", "Addition of items with different name: ${this.name} and ${other.name}")
            "${this.name} | ${other.name}"
        }
        val newQuantity = this.quantity + other.quantity
        val newSum = this._raw_sum + other._raw_sum
        // WARNING: this price may be not precise, as it's an integer division
        val newPriceForSingle = (newSum / newQuantity).toInt()
        return Item(
            qr = qr,
            name = newName,
            _raw_priceForSingle = newPriceForSingle,
            _raw_sum = newSum,
            quantity = newQuantity,
            positionInReceipt = min(this.positionInReceipt, other.positionInReceipt)
        )
    }
}

