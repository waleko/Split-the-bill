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
fun PhoneEnterScreen(sendLoginCode: (String) -> Unit) {
    SplitTheBillTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Column {
                var text by rememberSaveable { mutableStateOf("") }
                TextField(value = text, onValueChange = { text = it })
                Button(onClick = {
                    sendLoginCode(text)
                    Log.d("PhoneEnterActivity", "Sent!")
                }) {
                    Text("Send login code")
                }
            }
        }
    }
}

@Preview
@Composable
fun PhoneEnterPreview() {
    PhoneEnterScreen(sendLoginCode = { println(it) })
}