package com.example.calmy.data.repository

import com.example.calmy.data.mapper.toDomain
import com.example.calmy.data.remote.api.AuthApi
import com.example.calmy.data.remote.dto.RegisterRequest
import com.example.calmy.domain.model.AuthSession
import com.example.calmy.domain.repository.AuthRepository
import java.io.IOException
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {

    private var cachedSession: AuthSession? = null

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<AuthSession> {
        return try {
            val response = authApi.register(
                request = RegisterRequest(
                    name = name,
                    email = email,
                    password = password
                )
            )
            val session = response.toDomain()
            cachedSession = session
            // TODO: Replace in-memory cache with DataStore for persistent token storage.
            Result.success(session)
        } catch (exception: HttpException) {
            Result.failure(Exception("Registration failed: ${exception.code()}"))
        } catch (_: IOException) {
            Result.failure(Exception("Network error. Check backend connection."))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    fun getCachedSession(): AuthSession? = cachedSession
}
