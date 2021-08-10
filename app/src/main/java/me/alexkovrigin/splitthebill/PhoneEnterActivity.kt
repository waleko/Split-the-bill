package me.alexkovrigin.splitthebill

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.runBlocking
import me.alexkovrigin.splitthebill.data.Repository
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme

class PhoneEnterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = Repository.getInstance(this.applicationContext)
        setContent {
            SplitTheBillTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        var text by rememberSaveable { mutableStateOf("") }
                        TextField(value = text, onValueChange = {text = it})
                        Button(onClick = {
                            runBlocking {
                                repo.sendLoginCode(text)
                            }
                            Log.d("PhoneEnterActivity", "Sent!")
                        }) {
                            Text("Send login code")
                        }
                    }
                }
            }
        }
    }
}
