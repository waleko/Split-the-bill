package me.alexkovrigin.splitthebill

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.alexkovrigin.splitthebill.data.Repository
import me.alexkovrigin.splitthebill.services.api.ReceiptInfo
import me.alexkovrigin.splitthebill.util.Result

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repository.getInstance(app.applicationContext)

    val isAuthenticated: Boolean
        get() = repo.isAuthenticated

    private fun formatPhone(phone: String): String? {
        val spacesRemoved = phone
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
        val match = Regex("^(\\+7|8|7)(\\d{10})$").matchEntire(spacesRemoved) ?: return null
        return "+7${match.groupValues[2]}"
    }

    private fun <T : Any> generateCallbackFunction(
        mainBody: suspend () -> Result<T>,
        onSuccess: suspend (value: T) -> Unit = {},
        onFailure: suspend (exception: Throwable) -> Unit = {
            Log.e("MainActivityViewModel", "callback failed", it)
        }
    ) = viewModelScope.launch {
        val result = try {
            mainBody()
        } catch (e: Exception) {
            Result.Error(e)
        }
        when (result) {
            is Result.Success -> onSuccess(result.data)
            is Result.Error -> onFailure(result.exception)
        }
    }

    fun enterPhoneAsync(phone: String, onSuccess: suspend (Unit) -> Unit) =
        generateCallbackFunction(
            mainBody = {
                val formatted = formatPhone(phone) ?: error("Phone format is invalid")
                repo.sendLoginCode(formatted)
            },
            onSuccess = onSuccess
        )

    fun enterCodeAsync(phone: String, code: String, onSuccess: suspend (Unit) -> Unit) =
        generateCallbackFunction(
            mainBody = {
                val formatted = formatPhone(phone) ?: error("Phone format is invalid")
                repo.verifyPhoneWithCode(formatted, code)
            },
            onSuccess = onSuccess
        )

    fun loadReceiptAsync(qr: String, onSuccess: suspend (Unit) -> Unit) =
        generateCallbackFunction(
            mainBody = {
                repo.getReceipt(qr)
            },
            onSuccess = onSuccess
        )

    fun getReceiptFromDB(qr: String) = repo.loadReceipt(qr)

    /**
     * Debug only. Remove on production
     */
    fun clearCredentials() = repo.clearCredentials()
}