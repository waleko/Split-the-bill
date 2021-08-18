package me.alexkovrigin.splitthebill.services.api

import com.google.gson.annotations.SerializedName
import me.alexkovrigin.splitthebill.util.asRubles

data class ItemInfo(
    val name: String,
    @SerializedName("price")
    val _raw_priceForSingle: Int,
    @SerializedName("sum")
    val _raw_sum: Int,
    val quantity: Double
) {
    val displayPriceForSingle: String
        get() = _raw_priceForSingle.asRubles()
    val displaySum: String
        get() = _raw_sum.asRubles()
}
