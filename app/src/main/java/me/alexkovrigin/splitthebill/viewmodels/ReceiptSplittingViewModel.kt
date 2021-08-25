package me.alexkovrigin.splitthebill.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.services.ReceiptSplitting
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class ReceiptSplittingViewModel : ViewModel() {
    val users = mutableStateListOf<User>()

    lateinit var rc: ReceiptSplitting

    private var initialized: Boolean = false

    fun launchWithViewScope(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend () -> Unit
    ) = viewModelScope.launch(context = context) {
        block()
    }

    @Synchronized
    fun initialize(users: List<User>, items: List<Item>) {
        if (initialized)
            return
        initialized = true

        rc = ReceiptSplitting(users, items)
    }
}