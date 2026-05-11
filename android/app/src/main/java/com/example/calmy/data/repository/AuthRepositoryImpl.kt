package com.example.calmy.data.repository

import com.example.calmy.data.mapper.toDomain
import com.example.calmy.data.remote.api.AuthApi
import com.example.calmy.data.remote.dto.RegisterRequestDto
import com.example.calmy.domain.model.AuthSession
import com.example.calmy.domain.repository.AuthRepository
import java.io.IOException
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<AuthSession> {
        return try {
            val response = authApi.register(
                request = RegisterRequestDto(
                    name = name,
                    email = email,
                    password = password
                )
            )
            Result.success(response.toDomain())
        } catch (exception: HttpException) {
            Result.failure(Exception("Не удалось зарегистрироваться. Код ошибки: ${exception.code()}"))
        } catch (_: IOException) {
            Result.failure(Exception("Не удалось подключиться к серверу. Проверь, запущен ли backend."))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
