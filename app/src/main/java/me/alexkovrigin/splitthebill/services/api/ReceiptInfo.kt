package me.alexkovrigin.splitthebill.services.api

import com.google.gson.annotations.SerializedName

data class ReceiptInfo(
    val dateTime: String,
    val items: List<ItemInfo>,
    @SerializedName("retailPlace")
    val _retailPlace: String?
)
