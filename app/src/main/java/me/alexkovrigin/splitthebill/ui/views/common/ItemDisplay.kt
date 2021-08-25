package me.alexkovrigin.splitthebill.ui.views.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexkovrigin.splitthebill.data.entity.Item

/**
 * Card that shows [item] information
 */
@Composable
fun ItemDisplay(item: Item) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(
                    text = item.name,
                    fontStyle = MaterialTheme.typography.h3.fontStyle,
                    fontWeight = FontWeight.Bold
                )

            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${item.displayQuantity} pc",
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PriceText(value = item._raw_sum)
                    PricePerPiece(displayValue = item.displayPriceForSingle)
                }
            }
        }
    }
}

@Preview
@Composable
fun ItemDisplayPreview() {
    ItemDisplay(item = Examples.LongItem)
}