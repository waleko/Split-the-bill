package me.alexkovrigin.splitthebill

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import me.alexkovrigin.splitthebill.data.entity.User

class PayerSelectionViewModel : ViewModel() {
    val chipsModel = mutableStateListOf<User>()
}