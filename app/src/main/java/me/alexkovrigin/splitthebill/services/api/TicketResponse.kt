package me.alexkovrigin.splitthebill.services.api

data class TicketResponse(
    val ticket: _ticket
) {
    data class _doc(val receipt: Receipt)
    data class _ticket(val document: _doc)

    val receipt: Receipt
        get() = ticket.document.receipt
}
