package com.example.calmy.data.remote.api

import com.example.calmy.data.remote.dto.AuthResponseDto
import com.example.calmy.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponseDto
}
