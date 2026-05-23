package com.example.calmy.data.remote.api

import com.example.calmy.data.remote.dto.AuthResponseDto
import com.example.calmy.data.remote.dto.LoginRequestDto
import com.example.calmy.data.remote.dto.RegisterRequestDto
import com.example.calmy.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @GET("auth/me")
    suspend fun me(): UserDto
}
