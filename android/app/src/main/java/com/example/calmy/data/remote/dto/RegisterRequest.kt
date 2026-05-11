package com.example.calmy.data.remote.dto

data class RegisterRequest(
    // If backend register contract does not accept `name`, this field can be removed later.
    val name: String,
    val email: String,
    val password: String
)
