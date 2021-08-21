package me.alexkovrigin.splitthebill.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import me.alexkovrigin.splitthebill.PREF_REFRESH_TOKEN
import me.alexkovrigin.splitthebill.PREF_SESSION_ID
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.Receipt
import me.alexkovrigin.splitthebill.data.entity.ReceiptWithItems
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.services.api.FNSApi
import me.alexkovrigin.splitthebill.services.api.LoginCodeInfo
import me.alexkovrigin.splitthebill.services.api.QRCodeInfo
import me.alexkovrigin.splitthebill.services.api.ReceiptInfo
import me.alexkovrigin.splitthebill.services.api.RefreshSessionInfo
import me.alexkovrigin.splitthebill.services.api.RequestSMSInfo
import me.alexkovrigin.splitthebill.services.api.client_secret
import me.alexkovrigin.splitthebill.util.CallNotExecutedException
import me.alexkovrigin.splitthebill.util.Result
import me.alexkovrigin.splitthebill.util.SingletonHolder
import me.alexkovrigin.splitthebill.util.invokeAsync
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log
import kotlin.random.Random

class Repository private constructor(private val context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener, Authenticator {

    companion object : SingletonHolder<Repository, Context>({
        Repository(it.applicationContext)
    })

    val isAuthenticated: Boolean
        get() = getSessionId() != null && getRefreshToken() != null

    private val serverUrl = "https://irkkt-mobile.nalog.ru:8888" // FIXME

    private val authHeaders: Map<String, String>
        get() = mapOf(
            "Host" to serverUrl,
            "Accept" to "*/*",
            "Device-OS" to "iOS",
            "Device-Id" to "7C82010F-16CC-446B-8F66-FC4080C66523",
            "clientVersion" to "2.9.0",
            "Accept-Language" to "ru-RU;q=1, en-US;q=0.9",
            "User-Agent" to "billchecker/2.9.0 (iPhone; iOS 13.6; Scale/2.00)"
        )

    private val logTag = "Repository"

    private val okHttpClient = with(OkHttpClient.Builder()) {
        authenticator(this@Repository)
        build()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (!isRequestWithSessionId(response)) {
            return null
        }
        val token = updateSessionId() ?: return null
        return response.request().newBuilder().header("sessionId", token).build()
    }

    private fun isRequestWithSessionId(response: Response): Boolean =
        response.request().header("sessionId") != null

    private val api: FNSApi = buildFNSApi()

    private val dao = BillDatabase.getInstance(context).dao()

    private fun buildFNSApi(): FNSApi {
        return Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(FNSApi::class.java)
    }

    @Synchronized
    private fun updateSessionId(): String? {
        val refreshToken =
            getRefreshToken() ?: throw IllegalStateException("No refresh token for refresh")
        try {
            val response = api.refreshSession(
                RefreshSessionInfo(client_secret, refreshToken),
                authHeaders
            ).execute()
            if (response.isSuccessful) {
                val authInfo =
                    response.body() ?: throw IllegalStateException("Empty response during refresh")
                Log.i(logTag, "Successfully refreshed sessionId!")
                PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .edit()
                    .putString(PREF_SESSION_ID, authInfo.sessionId)
                    .putString(PREF_REFRESH_TOKEN, authInfo.refresh_token)
                    .apply()
                return authInfo.sessionId
            }
            // FIXME: if refresh token is invalid, redirect to phone enter
            Log.w(logTag, "Error during refresh token update: ${response.errorBody()}")
            return null
        } catch (t: Throwable) {
            Log.w(logTag, "Cannot update refresh token", t)
            return null
        }
    }

    private suspend fun <T : Any> simpleRequestTry(block: suspend () -> Result<T>): Result<T> {
        return try {
            block()
        } catch (e: CallNotExecutedException) {
            Log.w(logTag, "Request error: ${e.message}", e)
            Result.Error(e)
        } catch (e: Exception) {
            Log.e(logTag, e.message, e)
            Result.Error(e)
        }
    }

    suspend fun sendLoginCode(phone: String): Result<Unit> = simpleRequestTry {
        api.sendSMSRequest(RequestSMSInfo(client_secret, phone), authHeaders).invokeAsync()
        Result.Success(Unit)
    }

    suspend fun verifyPhoneWithCode(phone: String, code: String): Result<Unit> = simpleRequestTry {
        val authInfo =
            api.sendLoginCode(LoginCodeInfo(client_secret, phone, code), authHeaders).invokeAsync()
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SESSION_ID, authInfo.sessionId)
            .putString(PREF_REFRESH_TOKEN, authInfo.refresh_token)
            .apply()
        Result.Success(Unit)
    }

    suspend fun getReceipt(qr: String): Result<Unit> = simpleRequestTry {
        if (dao.doesReceiptWithQRExist(qr)) {
            Log.d(logTag, "Receipt $qr already loaded!")
            return@simpleRequestTry Result.Success(Unit)
        }

        val ticketIdResponse =
            api.getTicketId(QRCodeInfo(qr), getSessionIdOrEmpty(), authHeaders).invokeAsync()
        val ticketId = ticketIdResponse.id
        val response = api.getTicket(ticketId, getSessionIdOrEmpty(), authHeaders).invokeAsync()
        val receiptInfo = response.receipt
        dao.insertReceiptInfo(qr, receiptInfo)
        Result.Success(Unit)
    }

    fun loadReceipt(qr: String): LiveData<ReceiptWithItems?> = dao.receiptWithItemsByQR(qr)

    /**
     * Debug only. Remove on production
     */
    fun clearCredentials() {
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SESSION_ID, null)
            .putString(PREF_REFRESH_TOKEN, null)
            .apply()
    }

    private fun getSessionId() =
        PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SESSION_ID, null)

    private fun getSessionIdOrEmpty() = getSessionId() ?: ""

    private fun getRefreshToken() =
        PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_REFRESH_TOKEN, null)

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
    }

    fun getAllUsers(): LiveData<List<User>> = dao.allUsers()
}
