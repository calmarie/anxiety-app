package com.example.calmy.data.remote.api

import com.example.calmy.data.remote.dto.StatisticsResponseDto
import com.example.calmy.data.remote.dto.SyncThoughtsRequestDto
import com.example.calmy.data.remote.dto.ThoughtsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ThoughtsApi {
    @POST("thoughts/sync")
    suspend fun syncThoughts(@Body request: SyncThoughtsRequestDto): ThoughtsResponseDto

    @GET("thoughts")
    suspend fun getThoughts(): ThoughtsResponseDto

    @GET("thoughts/statistics")
    suspend fun getStatistics(): StatisticsResponseDto
}
