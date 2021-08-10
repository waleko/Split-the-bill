package me.alexkovrigin.splitthebill.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import me.alexkovrigin.splitthebill.PREF_REFRESH_TOKEN
import me.alexkovrigin.splitthebill.PREF_SESSION_ID
import me.alexkovrigin.splitthebill.services.api.FNSApi
import me.alexkovrigin.splitthebill.services.api.RefreshSessionInfo
import me.alexkovrigin.splitthebill.services.api.RequestSMSInfo
import me.alexkovrigin.splitthebill.services.api.client_secret
import me.alexkovrigin.splitthebill.services.api.defaultAuthHeaders
import me.alexkovrigin.splitthebill.util.CallNotExecutedException
import me.alexkovrigin.splitthebill.util.SingletonHolder
import me.alexkovrigin.splitthebill.util.Result
import me.alexkovrigin.splitthebill.util.invokeAsync
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository private constructor(private val context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener, Authenticator {

    companion object : SingletonHolder<Repository, Context>({
        Repository(it.applicationContext)
    })

    private val serverUrl = "https://irkkt-mobile.nalog.ru:8888" // FIXME

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

    private var api: FNSApi = buildFNSApi()

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
                defaultAuthHeaders
            ).execute()
            if (response.isSuccessful) {
                val authInfo =
                    response.body() ?: throw IllegalStateException("Empty response during refresh")
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

    suspend fun sendLoginCode(phone: String): Result<Unit> {
        return try {
            val sent = api.sendSMSRequest(RequestSMSInfo(client_secret, phone), defaultAuthHeaders).invokeAsync()
            Result.Success(sent)
        } catch (e: CallNotExecutedException) {
            Log.w(logTag, "Request error: ${e.message}", e)
            Result.Error(e)
        } catch (e: Exception) {
            Log.e(logTag, e.message, e)
            Result.Error(e)
        }
    }

    private fun getSessionId() =
        PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SESSION_ID, null)

    private fun getRefreshToken() =
        PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_REFRESH_TOKEN, null)

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {

    }
}
