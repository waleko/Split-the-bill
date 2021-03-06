package me.alexkovrigin.splitthebill.ui.views.login

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme
import me.alexkovrigin.splitthebill.viewmodels.MainActivityViewModel

@Composable
fun SMSCodeScreen(
    phone: String,
    navigateToHome: () -> Unit,
    viewModel: MainActivityViewModel = viewModel(MainActivityViewModel::class.java),
) {
    Column {
        var codeInput by rememberSaveable { mutableStateOf("") }
        var isError by rememberSaveable { mutableStateOf(false) }

        TextField(value = codeInput, maxLines = 1, isError = isError, onValueChange = {
            codeInput = it
            isError = Regex("\\d{4}").matchEntire(it) == null
        })
        Button(onClick = {
            viewModel.enterCodeAsync(phone, codeInput) {
                Log.d("SMSCodeScreen", "Code verified")
                navigateToHome()
            }
        }) {
            Text("Verify phone")
        }
    }
}

@Preview
@Composable
fun SMSCodeScreenPreview() {
    SplitTheBillTheme {
        Surface(color = MaterialTheme.colors.background) {
            SMSCodeScreen("+7 (800) 000-00-00", navigateToHome = {})
        }
    }
}