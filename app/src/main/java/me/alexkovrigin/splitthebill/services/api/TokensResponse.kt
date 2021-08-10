package me.alexkovrigin.splitthebill.services.api

data class TokensResponse(
    val sessionId: String,
    val refresh_token: String
)