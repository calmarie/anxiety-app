package com.example.calmy.presentation.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calmy.domain.model.AnxietyTypeCount
import com.example.calmy.domain.model.PeriodStatistics
import com.example.calmy.domain.model.Statistics
import com.example.calmy.domain.model.SupportMessage
import com.example.calmy.presentation.common.CalmyBottomNavDestination
import com.example.calmy.presentation.common.CalmyBottomNavigation
import com.example.calmy.presentation.common.CalmyCard
import com.example.calmy.presentation.common.CalmyColors
import com.example.calmy.presentation.common.CalmyPrimaryButton
import com.example.calmy.presentation.common.CalmyScreenBackground
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StatisticsScreen(
    state: StateFlow<StatisticsState>,
    onRefreshClick: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenAddThought: () -> Unit,
    onOpenThoughtList: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState = state.collectAsStateWithLifecycle().value

    CalmyScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CalmyBottomNavigation(
                    selectedDestination = CalmyBottomNavDestination.Statistics,
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
                        text = "Статистика",
                        style = MaterialTheme.typography.displaySmall,
                        color = CalmyColors.PrimaryDark
                    )
                    Text(
                        text = "Свежая картина недели, месяца и года",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CalmyColors.TextSecondary
                    )
                }

                if (uiState.isLoading) {
                    LoadingCard()
                }

                if (uiState.error != null) {
                    MessageCard(
                        text = uiState.error,
                        color = CalmyColors.Error
                    )
                    CalmyPrimaryButton(
                        text = "Повторить",
                        onClick = onRefreshClick,
                        leadingIcon = Icons.Filled.Refresh
                    )
                } else {
                    uiState.statistics?.let { statistics ->
                        StatisticsContent(
                            statistics = statistics,
                            supportMessage = uiState.supportMessage,
                            supportNotice = uiState.supportNotice
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingCard() {
    CalmyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                color = CalmyColors.Primary,
                strokeWidth = 3.dp
            )
            Text(
                text = "Загружаю статистику",
                style = MaterialTheme.typography.bodyMedium,
                color = CalmyColors.TextSecondary
            )
        }
    }
}

@Composable
private fun StatisticsContent(
    statistics: Statistics,
    supportMessage: SupportMessage?,
    supportNotice: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        PeriodSummary(
            title = "Неделя",
            period = statistics.week
        )
        PeriodSummary(
            title = "Месяц",
            period = statistics.month
        )
        PeriodSummary(
            title = "Год",
            period = statistics.year
        )
        WeekDetails(period = statistics.week)
        BackendTodayColumn(
            supportMessage = supportMessage,
            supportNotice = supportNotice
        )
    }
}

@Composable
private fun BackendTodayColumn(
    supportMessage: SupportMessage?,
    supportNotice: String?
) {
    CalmyCard(useSoftSurface = true) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Сегодня",
                style = MaterialTheme.typography.titleMedium,
                color = CalmyColors.TextPrimary
            )
            if (supportMessage == null) {
                Text(
                    text = supportNotice ?: "Данные за сегодня появятся после сохранения настроек уведомлений",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CalmyColors.TextSecondary
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatChip(
                        label = "Средняя тревога",
                        value = formatNullableAverage(supportMessage.averageDailyAnxiety),
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label = "Диапазон",
                        value = formatAnxietyRange(supportMessage.anxietyRange),
                        modifier = Modifier.weight(1f)
                    )
                }
                StatChip(
                    label = "Частота уведомлений",
                    value = formatBackendFrequency(supportMessage.frequencyMinutes),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PeriodSummary(
    title: String,
    period: PeriodStatistics
) {
    CalmyCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = CalmyColors.TextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(
                    label = "Дней с записями",
                    value = period.dailyDynamics.size.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Записей",
                    value = period.entriesCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            StatChip(
                label = "Средний уровень тревоги",
                value = formatAverage(period.averageAnxietyLevel),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WeekDetails(period: PeriodStatistics) {
    val mostFrequentType = period.anxietyTypeFrequencies.maxByOrNull { value -> value.count }
    CalmyCard(useSoftSurface = true) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Детали недели",
                style = MaterialTheme.typography.titleMedium,
                color = CalmyColors.TextPrimary
            )
            Text(
                text = "Самая частая категория: ${formatType(mostFrequentType)}",
                style = MaterialTheme.typography.bodyMedium,
                color = CalmyColors.TextSecondary
            )
            Text(
                text = "Наиболее тревожное время: ${formatTime(period.mostAnxiousTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = CalmyColors.TextSecondary
            )
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = CalmyColors.SurfaceSoft,
        border = BorderStroke(1.dp, CalmyColors.Stroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = CalmyColors.TextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = CalmyColors.TextMuted
            )
        }
    }
}

@Composable
private fun MessageCard(
    text: String,
    color: Color
) {
    CalmyCard(
        useSoftSurface = true,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

private fun formatAverage(value: Double): String {
    return "%.1f".format(value)
}

private fun formatNullableAverage(value: Double?): String {
    return value?.let { formatAverage(it) } ?: "нет данных"
}

private fun formatAnxietyRange(value: String?): String {
    return when (value) {
        "low" -> "низкий"
        "medium" -> "средний"
        "high" -> "высокий"
        else -> "нет данных"
    }
}

private fun formatBackendFrequency(value: Int?): String {
    return when (value) {
        null -> "нет данных"
        1 -> "каждую минуту"
        30 -> "каждые 30 минут"
        60 -> "каждый час"
        else -> if (value % 60 == 0) {
            "каждые ${value / 60} ч"
        } else {
            "каждые $value мин"
        }
    }
}

private fun formatType(value: AnxietyTypeCount?): String {
    return value?.anxietyType?.displayName ?: "нет данных"
}

private fun formatTime(value: String?): String {
    return when (value) {
        "night" -> "ночь"
        "morning" -> "утро"
        "day" -> "день"
        "evening" -> "вечер"
        else -> "нет данных"
    }
}
