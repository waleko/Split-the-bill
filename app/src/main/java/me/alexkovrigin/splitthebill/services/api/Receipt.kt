package me.alexkovrigin.splitthebill.services.api

data class Receipt(
    val dateTime: String,
    val items: List<Item>
)
