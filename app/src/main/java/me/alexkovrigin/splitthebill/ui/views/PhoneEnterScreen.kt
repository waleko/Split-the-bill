package me.alexkovrigin.splitthebill.ui.views

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
import me.alexkovrigin.splitthebill.MainActivityViewModel
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme

@Composable
fun PhoneEnterScreen(
    navigateToEnterCode: (phone: String) -> Unit,
    viewModel: MainActivityViewModel = viewModel(MainActivityViewModel::class.java),
) {
    Column {
        var phoneInput by rememberSaveable { mutableStateOf("") }
        TextField(value = phoneInput, onValueChange = { phoneInput = it })
        Button(onClick = {
            viewModel.enterPhoneAsync(phoneInput, onSuccess = {
                Log.d("PhoneEnterActivity", "SMS verification code sent")
                navigateToEnterCode(phoneInput)
            })
        }) {
            Text("Send login code")
        }
    }
}

@Preview
@Composable
fun PhoneEnterPreview() {
    SplitTheBillTheme {
        Surface(color = MaterialTheme.colors.background) {
            PhoneEnterScreen(navigateToEnterCode = {})
        }
    }
}