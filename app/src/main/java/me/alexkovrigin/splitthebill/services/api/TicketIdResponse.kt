package me.alexkovrigin.splitthebill.services.api

data class TicketIdResponse(
    val kind: String,
    val id: String,
    val status: Int
)
