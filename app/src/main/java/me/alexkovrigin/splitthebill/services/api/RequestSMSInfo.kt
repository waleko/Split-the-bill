package me.alexkovrigin.splitthebill.services.api

data class RequestSMSInfo(
    val client_secret: String,
    val phone: String
)
