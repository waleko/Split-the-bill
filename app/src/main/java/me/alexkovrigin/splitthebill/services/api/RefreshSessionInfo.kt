package me.alexkovrigin.splitthebill.services.api

data class RefreshSessionInfo(
    val client_secret: String,
    val refresh_token: String
)