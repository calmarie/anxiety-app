package com.example.calmy.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calmy.presentation.common.CalmyBottomNavDestination
import com.example.calmy.presentation.common.CalmyBottomNavigation
import com.example.calmy.presentation.common.CalmyCard
import com.example.calmy.presentation.common.CalmyColors
import com.example.calmy.presentation.common.CalmyPrimaryButton
import com.example.calmy.presentation.common.CalmyScreenBackground
import com.example.calmy.presentation.common.CalmySecondaryButton
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SettingsScreen(
    state: StateFlow<SettingsState>,
    onPresetFrequencySelected: (Int) -> Unit,
    onCustomHoursChanged: (String) -> Unit,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onTestFrequencyClick: () -> Unit,
    onSaveClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenAddThought: () -> Unit,
    onOpenThoughtList: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState = state.collectAsStateWithLifecycle().value

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    CalmyScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CalmyBottomNavigation(
                    selectedDestination = CalmyBottomNavDestination.Settings,
                    onHomeClick = onOpenHome,
                    onAddThoughtClick = onOpenAddThought,
                    onThoughtListClick = onOpenThoughtList,
                    onStatisticsClick = onOpenStatistics,
                    onSettingsClick = onOpenSettings
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.displaySmall,
                        color = CalmyColors.PrimaryDark
                    )
                    Text(
                        text = "Частота уведомлений и выход из аккаунта",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CalmyColors.TextSecondary
                    )
                }

                CalmyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = uiState.notificationsEnabled,
                                onCheckedChange = onNotificationsEnabledChanged,
                                enabled = !uiState.isLoading && !uiState.isSaving,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CalmyColors.Primary,
                                    uncheckedColor = CalmyColors.TextMuted,
                                    checkmarkColor = CalmyColors.Surface
                                )
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Уведомления",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = CalmyColors.TextPrimary
                                )
                                Text(
                                    text = if (uiState.notificationsEnabled) {
                                        formatFrequency(uiState.selectedFrequencyMinutes)
                                    } else {
                                        "Уведомления выключены"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CalmyColors.TextSecondary
                                )
                            }
                        }
                        FrequencyPresetGrid(
                            selectedFrequencyMinutes = uiState.selectedFrequencyMinutes,
                            enabled = uiState.notificationsEnabled && !uiState.isLoading && !uiState.isSaving,
                            onSelect = onPresetFrequencySelected
                        )
                        CustomHoursField(
                            value = uiState.customHoursText,
                            enabled = uiState.notificationsEnabled && !uiState.isLoading && !uiState.isSaving,
                            onValueChange = onCustomHoursChanged
                        )
                        CalmySecondaryButton(
                            text = "Тест: раз в минуту",
                            onClick = onTestFrequencyClick,
                            enabled = !uiState.isLoading && !uiState.isSaving
                        )
                        CalmyPrimaryButton(
                            text = "Сохранить настройки",
                            onClick = onSaveClick,
                            enabled = !uiState.isLoading && !uiState.isSaving,
                            isLoading = uiState.isSaving,
                            leadingIcon = Icons.Filled.Save
                        )
                    }
                }

                if (uiState.message != null) {
                    CalmyCard(
                        useSoftSurface = true,
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (uiState.isError) CalmyColors.Error else CalmyColors.Success
                        )
                    }
                }

                CalmySecondaryButton(
                    text = "Выйти из аккаунта",
                    onClick = onLogoutClick,
                    enabled = !uiState.isLoggingOut,
                    isLoading = uiState.isLoggingOut,
                    leadingIcon = Icons.AutoMirrored.Filled.Logout
                )
            }
        }
    }
}

@Composable
private fun FrequencyPresetGrid(
    selectedFrequencyMinutes: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            30 to "30 минут",
            60 to "1 час",
            180 to "3 часа",
            360 to "6 часов"
        ).chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { (minutes, label) ->
                    FrequencyButton(
                        text = label,
                        selected = selectedFrequencyMinutes == minutes,
                        enabled = enabled,
                        onClick = { onSelect(minutes) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FrequencyButton(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = when {
            selected -> CalmyColors.PrimaryLight
            enabled -> CalmyColors.Surface
            else -> CalmyColors.SurfaceSoft
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) CalmyColors.Primary else CalmyColors.Stroke
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) CalmyColors.TextPrimary else CalmyColors.TextMuted
            )
        }
    }
}

@Composable
private fun CustomHoursField(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Своё время",
            style = MaterialTheme.typography.labelMedium,
            color = CalmyColors.TextSecondary
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = CalmyColors.TextPrimary),
            placeholder = {
                Text(
                    text = "Например, 2.5 часа",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CalmyColors.TextMuted
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CalmyColors.Surface,
                unfocusedContainerColor = CalmyColors.Surface,
                disabledContainerColor = CalmyColors.Surface,
                focusedBorderColor = CalmyColors.Primary,
                unfocusedBorderColor = CalmyColors.Stroke,
                disabledBorderColor = CalmyColors.Stroke,
                cursorColor = CalmyColors.Primary,
                focusedTextColor = CalmyColors.TextPrimary,
                unfocusedTextColor = CalmyColors.TextPrimary,
                disabledTextColor = CalmyColors.TextMuted
            )
        )
    }
}

private fun formatFrequency(minutes: Int): String {
    return when {
        minutes == 1 -> "Каждую минуту"
        minutes < 60 -> "Каждые $minutes минут"
        minutes == 60 -> "Каждый час"
        minutes % 60 == 0 -> "Каждые ${minutes / 60} ${hourWord(minutes / 60)}"
        else -> "Каждые $minutes минут"
    }
}

private fun hourWord(hours: Int): String {
    return when {
        hours % 10 == 1 && hours % 100 != 11 -> "час"
        hours % 10 in 2..4 && hours % 100 !in 12..14 -> "часа"
        else -> "часов"
    }
}
