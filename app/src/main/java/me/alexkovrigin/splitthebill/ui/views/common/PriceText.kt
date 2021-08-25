package me.alexkovrigin.splitthebill.ui.views.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme
import me.alexkovrigin.splitthebill.utilities.asRubles

/**
 * Displays raw price (in cents) in rubles. Basically divides [value] by 100.
 *
 * If [value] is null, doesn't show anything.
 */
@Composable
fun PriceText(value: Int?) {
    if (value == null)
        return
    PriceText(displayValue = value.asRubles())
}

@Composable
fun PriceText(displayValue: String) {
    CommonPriceText(
        displayValue = displayValue,
        priceSpanStyle = MaterialTheme.typography.body2.toSpanStyle()
            .copy(fontWeight = FontWeight.Bold)
    )
}

/**
 * Generates text for a price.
 *
 * @param displayValue Price without currency, e.g. "300" or "123,45"
 * @param priceSpanStyle span style for [displayValue]
 * @param currency The unit of measurements for [displayValue].
 */
@Composable
private fun CommonPriceText(
    displayValue: String,
    priceSpanStyle: SpanStyle,
    currency: String = "rub." // TODO move to strings
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = priceSpanStyle
            ) {
                append(displayValue)
                append(" ")
            }
            append(currency)
        }
    )
}

@Composable
fun PricePerPiece(displayValue: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = MaterialTheme.typography.caption.toSpanStyle().copy(
                    color = Color.Gray,
                    fontSize = 8.sp
                )
            ) {
                append(displayValue)
                append(" rub/pc") // TODO: move to strings
            }
        }
    )
}

@Preview
@Composable
fun PriceTextPreview() {
    SplitTheBillTheme {
        Surface(color = MaterialTheme.colors.background) {
            Column {
                PriceText(value = null)
                PriceText(value = 0)
                PriceText(value = 31)
                PriceText(value = 2300)
                PricePerPiece(displayValue = "300")
            }
        }
    }
}