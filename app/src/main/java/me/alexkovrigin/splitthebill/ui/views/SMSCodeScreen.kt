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
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme

@Composable
fun SMSCodeScreen(phone: String, verifyPhoneWithCode: (phone: String, code: String) -> Unit) {
    SplitTheBillTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Column {
                var text by rememberSaveable { mutableStateOf("") }
                var isError by rememberSaveable { mutableStateOf(false) }
                TextField(value = text, maxLines = 1, isError = isError, onValueChange = {
                    text = it
                    isError = Regex("\\d{4}").matchEntire(it) == null
                })
                Button(onClick = {
                    verifyPhoneWithCode(phone, text)
                    Log.d("SMSCodeScreen", "Code verified")
                }) {
                    Text("Verify phone")
                }
            }
        }
    }
}

@Preview
@Composable
fun SMSCodeScreenPreview() {
    SMSCodeScreen("") { a, b -> println("$a $b") }
}