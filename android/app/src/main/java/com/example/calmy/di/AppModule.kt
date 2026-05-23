package com.example.calmy.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calmy.BuildConfig
import com.example.calmy.data.config.LocalDataConfig
import com.example.calmy.data.local.preferences.CalmLevelStorage
import com.example.calmy.data.local.preferences.NotificationPreferencesStorage
import com.example.calmy.data.local.session.DataStoreSessionStorage
import com.example.calmy.data.local.thoughts.AppDatabase
import com.example.calmy.data.remote.NetworkConfig
import com.example.calmy.data.remote.api.AuthApi
import com.example.calmy.data.remote.api.NotificationsApi
import com.example.calmy.data.remote.api.ThoughtsApi
import com.example.calmy.data.remote.interceptor.AuthInterceptor
import com.example.calmy.data.repository.AuthRepositoryImpl
import com.example.calmy.data.repository.LocalAuthRepository
import com.example.calmy.data.repository.LocalNotificationsRepository
import com.example.calmy.data.repository.LocalThoughtsRepository
import com.example.calmy.data.repository.NotificationsRepositoryImpl
import com.example.calmy.data.repository.ThoughtsRepositoryImpl
import com.example.calmy.domain.repository.AuthRepository
import com.example.calmy.domain.repository.NotificationsRepository
import com.example.calmy.domain.repository.ThoughtsRepository
import com.example.calmy.presentation.addthought.AddThoughtViewModel
import com.example.calmy.presentation.home.HomeViewModel
import com.example.calmy.presentation.login.LoginViewModel
import com.example.calmy.presentation.register.RegisterViewModel
import com.example.calmy.presentation.settings.SettingsViewModel
import com.example.calmy.presentation.splash.SplashViewModel
import com.example.calmy.presentation.statistics.StatisticsViewModel
import com.example.calmy.presentation.thoughtlist.ThoughtListViewModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {
    fun createDependencies(context: Context): AppDependencies {
        val appContext = context.applicationContext
        val sessionStorage = DataStoreSessionStorage(appContext)
        val calmLevelStorage = CalmLevelStorage(appContext)
        val notificationPreferencesStorage = NotificationPreferencesStorage(appContext)
        val gson = GsonBuilder().create()
        val database = AppDatabase.create(appContext, gson)
        if (LocalDataConfig.USE_LOCAL_DATA) {
            return AppDependencies(
                authRepository = LocalAuthRepository(sessionStorage),
                notificationsRepository = LocalNotificationsRepository(
                    preferencesStorage = notificationPreferencesStorage,
                    thoughtDao = database.thoughtDao(),
                    sessionStorage = sessionStorage
                ),
                thoughtsRepository = LocalThoughtsRepository(
                    thoughtDao = database.thoughtDao(),
                    sessionStorage = sessionStorage
                ),
                calmLevelStorage = calmLevelStorage
            )
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionStorage))
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        val okHttpClient = okHttpClientBuilder.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val authRepository = AuthRepositoryImpl(
            authApi = retrofit.create(AuthApi::class.java),
            sessionStorage = sessionStorage
        )
        val thoughtsRepository = ThoughtsRepositoryImpl(
            thoughtsApi = retrofit.create(ThoughtsApi::class.java),
            thoughtDao = database.thoughtDao(),
            sessionStorage = sessionStorage
        )
        val notificationsRepository = NotificationsRepositoryImpl(
            notificationsApi = retrofit.create(NotificationsApi::class.java),
            preferencesStorage = notificationPreferencesStorage
        )

        return AppDependencies(
            authRepository = authRepository,
            notificationsRepository = notificationsRepository,
            thoughtsRepository = thoughtsRepository,
            calmLevelStorage = calmLevelStorage
        )
    }

    fun provideSplashViewModelFactory(dependencies: AppDependencies): ViewModelProvider.Factory {
        return viewModelFactory {
            SplashViewModel(dependencies.authRepository)
        }
    }

    fun provideRegisterViewModelFactory(dependencies: AppDependencies): ViewModelProvider.Factory {
        return viewModelFactory {
            RegisterViewModel(dependencies.authRepository)
        }
    }

    fun provideLoginViewModelFactory(dependencies: AppDependencies): ViewModelProvider.Factory {
        return viewModelFactory {
            LoginViewModel(dependencies.authRepository)
        }
    }

    fun provideHomeViewModelFactory(dependencies: AppDependencies): ViewModelProvider.Factory {
        return viewModelFactory {
            HomeViewModel(
                authRepository = dependencies.authRepository,
                thoughtsRepository = dependencies.thoughtsRepository,
                calmLevelStorage = dependencies.calmLevelStorage
            )
        }
    }

    fun provideAddThoughtViewModelFactory(dependencies: AppDependencies): ViewModelProvider.Factory {
        return viewModelFactory {
            AddThoughtViewModel(dependencies.thoughtsRepository)
        }
    }

    fun provideThoughtListViewModelFactory(dependencies: AppDependencies): ViewModelProvider.Factory {
        return viewModelFactory {
            ThoughtListViewModel(dependencies.thoughtsRepository)
        }
    }

    fun provideStatisticsViewModelFactory(dependencies: AppDependencies): ViewModelProvider.Factory {
        return viewModelFactory {
            StatisticsViewModel(
                thoughtsRepository = dependencies.thoughtsRepository,
                notificationsRepository = dependencies.notificationsRepository
            )
        }
    }

    fun provideSettingsViewModelFactory(dependencies: AppDependencies): ViewModelProvider.Factory {
        return viewModelFactory {
            SettingsViewModel(
                notificationsRepository = dependencies.notificationsRepository,
                authRepository = dependencies.authRepository
            )
        }
    }

    private fun <VM : ViewModel> viewModelFactory(builder: () -> VM): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return builder() as T
            }
        }
    }
}

data class AppDependencies(
    val authRepository: AuthRepository,
    val notificationsRepository: NotificationsRepository,
    val thoughtsRepository: ThoughtsRepository,
    val calmLevelStorage: CalmLevelStorage
)
