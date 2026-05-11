package com.example.calmy.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calmy.data.remote.api.AuthApi
import com.example.calmy.data.repository.AuthRepositoryImpl
import com.example.calmy.domain.repository.AuthRepository
import com.example.calmy.presentation.register.RegisterViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://10.0.2.2:8080/"

object AppModule {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            // TODO: Add Authorization Bearer token interceptor after login/session refresh is implemented.
            .build()
    }

    private val gson: Gson by lazy {
        Gson()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi = authApi)
    }

    fun provideRegisterViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                    return RegisterViewModel(authRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
