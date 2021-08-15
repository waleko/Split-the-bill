package me.alexkovrigin.splitthebill.services.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

interface FNSApi {

    @POST("/v2/auth/phone/request")
    fun sendSMSRequest(
        @Body requestSMSInfo: RequestSMSInfo,
        @HeaderMap headerMap: Map<String, String>
    ): Call<Unit>

    @POST("/v2/auth/phone/verify")
    fun sendLoginCode(
        @Body loginCodeInfo: LoginCodeInfo,
        @HeaderMap headerMap: Map<String, String>
    ): Call<TokensResponse>

    @POST("/v2/ticket")
    fun getTicketId(
        @Body qrCodeInfo: QRCodeInfo,
        @Header("sessionId") sessionId: String,
        @HeaderMap headerMap: Map<String, String>
    ): Call<TicketIdResponse>

    @GET("/v2/tickets/{ticketId}")
    fun getTicket(
        @Path("ticketId") ticketId: String,
        @Header("sessionId") sessionId: String,
        @HeaderMap headerMap: Map<String, String>
    ): Call<TicketResponse>

    @POST("/v2/mobile/users/refresh")
    fun refreshSession(
        @Body refreshSessionInfo: RefreshSessionInfo,
        @HeaderMap headerMap: Map<String, String>
    ): Call<TokensResponse>
}