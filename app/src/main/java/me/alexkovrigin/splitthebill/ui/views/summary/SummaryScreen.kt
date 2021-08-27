package me.alexkovrigin.splitthebill.ui.views.summary

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import me.alexkovrigin.splitthebill.viewmodels.MainActivityViewModel

@Composable
fun SummaryScreen(
    uid: String,
    viewModel: MainActivityViewModel = viewModel(MainActivityViewModel::class.java)
) {
    val state = viewModel.getSplitReceiptWithItemsAndSplitting(uid).observeAsState()
    val splittingResult = state.value ?: return

    Column {
        Text(text = splittingResult.qr)
        Text(text = splittingResult.receiptWithItems.displayRetailPlace)
        Text(text = splittingResult.receiptWithItems.items.first().name)
        Text(text = splittingResult.splitting.keys.first().displayName)
    }
}