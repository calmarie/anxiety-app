package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnxietyTypeCountDto(
    @SerializedName("anxiety_type")
    val anxietyType: String,
    val count: Int
)
