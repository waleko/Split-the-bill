package me.alexkovrigin.splitthebill.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import me.alexkovrigin.splitthebill.services.api.Item
import me.alexkovrigin.splitthebill.services.api.Receipt
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme

@Composable
fun QRInfoScreen(qr: String, qrToReceipt: (setQR: (receipt: Receipt) -> Unit) -> Unit) {
    SplitTheBillTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Column {
                var text by rememberSaveable { mutableStateOf("") }
                var products by mutableStateOf(listOf<Item>())
                qrToReceipt {
                    text = it.dateTime
                    products = it.items
                }
                Text(text = text)
                LazyColumn {
                    items(products) {
                        Row {
                            Text(text = it.name)
                            Text(text = it.sum.toString())
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun QRInfoScreenPreview() {
    QRInfoScreen(qr = "skibidi") { }
}