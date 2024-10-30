package com.jgeek00.ServerStatus.services

import retrofit2.Response
import retrofit2.http.GET

interface ApiClient {
    @GET("status")
    suspend fun getStatus(): Response<Any>
}
