package me.alexkovrigin.splitthebill.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    label: String,
    color: Color,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.padding(8.dp)) {
        Surface(
            elevation = 3.dp,
            shape = MaterialTheme.shapes.small,
            color = color
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    label,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.button
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "close",
                        modifier = Modifier
                            .height(20.dp)
                            .padding(horizontal = 1.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChipPreview() {
    val color = MaterialTheme.colors.primary
    Chip(label = "Alex", color = color) {}
}