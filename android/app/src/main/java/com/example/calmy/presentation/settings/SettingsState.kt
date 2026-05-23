package com.example.calmy.presentation.settings

data class SettingsState(
    val selectedFrequencyMinutes: Int = 180,
    val customHoursText: String = "",
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)
