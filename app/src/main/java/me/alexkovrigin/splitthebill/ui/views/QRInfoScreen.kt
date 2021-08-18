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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import me.alexkovrigin.splitthebill.MainActivityViewModel
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme

@Composable
fun QRInfoScreen(
    qr: String,
    viewModel: MainActivityViewModel = viewModel(MainActivityViewModel::class.java)
) {
    Column {
        val state = viewModel.getReceiptFromDB(qr).observeAsState()
        val receipt by rememberSaveable { state }
        Text(text = receipt?.dateTime.orEmpty())
        LazyColumn {
            items(receipt?.items.orEmpty()) {
                Row {
                    Text(text = it.name)
                    Text(text = it.displaySum)
                }
            }
        }
    }
}

@Preview
@Composable
fun QRInfoScreenPreview() {
    SplitTheBillTheme {
        Surface(color = MaterialTheme.colors.background) {
            QRInfoScreen(qr = "skibidi")
        }
    }
}