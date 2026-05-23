package com.example.calmy.data.repository

import com.example.calmy.data.local.session.SessionStorage
import com.example.calmy.data.mapper.toDomain
import com.example.calmy.data.remote.api.AuthApi
import com.example.calmy.data.remote.dto.AuthResponseDto
import com.example.calmy.data.remote.dto.LoginRequestDto
import com.example.calmy.data.remote.dto.RegisterRequestDto
import com.example.calmy.domain.model.AuthSession
import com.example.calmy.domain.model.User
import com.example.calmy.domain.repository.AuthRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionStorage: SessionStorage
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<AuthSession> {
        return runAuthRequest {
            authApi.register(
                request = RegisterRequestDto(
                    email = email,
                    name = name,
                    password = password
                )
            )
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> {
        return runAuthRequest {
            authApi.login(
                request = LoginRequestDto(
                    email = email,
                    password = password
                )
            )
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            Result.success(authApi.me().toDomain())
        } catch (_: IOException) {
            val user = sessionStorage.getSession()?.user
            if (user == null) {
                Result.failure(Exception("Не удалось получить данные пользователя"))
            } else {
                Result.success(user)
            }
        } catch (exception: HttpException) {
            Result.failure(Exception("Не удалось получить профиль. Код ошибки: ${exception.code()}"))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getCurrentToken(): String? {
        return sessionStorage.getToken()
    }

    override suspend fun logout() {
        sessionStorage.clearSession()
    }

    override fun observeToken(): Flow<String?> {
        return sessionStorage.observeToken()
    }

    private suspend fun runAuthRequest(
        request: suspend () -> AuthResponseDto
    ): Result<AuthSession> {
        return try {
            val session = request().toDomain()
            sessionStorage.saveSession(session)
            Result.success(session)
        } catch (exception: HttpException) {
            Result.failure(Exception("Сервер вернул ошибку: ${exception.code()}"))
        } catch (_: IOException) {
            Result.failure(Exception("Не удалось подключиться к серверу. Проверь подключение и попробуй ещё раз."))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
