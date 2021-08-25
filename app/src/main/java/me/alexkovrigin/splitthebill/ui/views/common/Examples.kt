package me.alexkovrigin.splitthebill.ui.views.common

import me.alexkovrigin.splitthebill.data.entity.Item

/**
 * Examples to be shown in previews
 */
object Examples {
    val SimpleItem = Item(
        qr = "qr-code",
        name = "Banana",
        _raw_priceForSingle = 100,
        _raw_sum = 300,
        quantity = 3.0,
        positionInReceipt = 0
    )

    val LongItem = Item(
        qr = "qr",
        name = "VERYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" +
            "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY LONG Item name",
        _raw_priceForSingle = 32139,
        _raw_sum = 3000023,
        quantity = 21.0,
        positionInReceipt = 0
    )
}