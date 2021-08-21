package me.alexkovrigin.splitthebill.ui.views

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import me.alexkovrigin.splitthebill.MainActivityViewModel
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme
import me.alexkovrigin.splitthebill.util.PaletteUtils

@Composable
fun PayerSelectScreen(
    qr: String,
    navigateToReceiptSplitting: (qr: String, payers: List<User>) -> Unit,
    viewModel: MainActivityViewModel = viewModel(MainActivityViewModel::class.java),
) {
    val users by viewModel.getAllUsers().observeAsState(listOf())
    ConstraintLayout {
        LazyColumn {
            items(users) {
                val colors = CheckboxDefaults.colors(
                    checkedColor = PaletteUtils.pickColorForUser(0)
                )
                val colorState = remember { mutableStateOf(colors) }
                UserCard(user = it, checkBoxColorsState = , checkBoxClicked = )
            }
        }
    }
}

@Preview
@Composable
fun PayerSelectScreen() {
    SplitTheBillTheme {
        Surface(color = MaterialTheme.colors.background) {
            PayerSelectScreen(
                qr = "qr-code-here",
                navigateToReceiptSplitting = {_, _ -> }
            )
        }
    }
}