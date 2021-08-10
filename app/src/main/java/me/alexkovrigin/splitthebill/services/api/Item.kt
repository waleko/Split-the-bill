package me.alexkovrigin.splitthebill.services.api

data class Item(
    val name: String,
    val price: Int, // FIXME: make double
    val sum: Int
)
