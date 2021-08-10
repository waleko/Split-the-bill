package me.alexkovrigin.splitthebill.services.api

data class LoginCodeInfo(
    val client_secret: String,
    val phone: String,
    val code: String
)
