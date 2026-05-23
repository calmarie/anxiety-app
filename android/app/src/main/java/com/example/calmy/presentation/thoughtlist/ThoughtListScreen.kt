package com.example.calmy.presentation.thoughtlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calmy.core.CalmyDateTime
import com.example.calmy.domain.model.Thought
import com.example.calmy.presentation.common.CalmyBottomNavDestination
import com.example.calmy.presentation.common.CalmyBottomNavigation
import com.example.calmy.presentation.common.CalmyCard
import com.example.calmy.presentation.common.CalmyColors
import com.example.calmy.presentation.common.CalmyPrimaryButton
import com.example.calmy.presentation.common.CalmyScreenBackground
import com.example.calmy.presentation.common.CalmySecondaryButton
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ThoughtListScreen(
    state: StateFlow<ThoughtListState>,
    onRefreshClick: () -> Unit,
    onAddThoughtClick: () -> Unit,
    onDeleteThoughtClick: (String) -> Unit,
    onOpenHome: () -> Unit,
    onOpenAddThought: () -> Unit,
    onOpenThoughtList: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState = state.collectAsStateWithLifecycle().value
    val expandedMonths = remember { mutableStateMapOf<String, Boolean>() }

    CalmyScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CalmyBottomNavigation(
                    selectedDestination = CalmyBottomNavDestination.ThoughtList,
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
                        text = "Записи",
                        style = MaterialTheme.typography.displaySmall,
                        color = CalmyColors.PrimaryDark
                    )
                    Text(
                        text = "Все сохранённые мысли в одном месте",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CalmyColors.TextSecondary
                    )
                }

                if (uiState.message != null) {
                    MessageBlock(
                        text = uiState.message,
                        color = if (uiState.isShowingCachedData) CalmyColors.TextSecondary else CalmyColors.Success
                    )
                }

                if (uiState.thoughts.isEmpty() && !uiState.isLoading) {
                    CalmyCard {
                        Text(
                            text = "Пока нет записей. Новая мысль появится здесь после сохранения.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CalmyColors.TextSecondary
                        )
                    }
                }

                groupedThoughts(uiState.thoughts).forEach { month ->
                    val isExpanded = expandedMonths[month.key] ?: true
                    MonthFolderCard(
                        month = month,
                        isExpanded = isExpanded,
                        onDeleteThoughtClick = onDeleteThoughtClick,
                        onToggle = {
                            expandedMonths[month.key] = !isExpanded
                        }
                    )
                }

                CalmyPrimaryButton(
                    text = "Добавить мысль",
                    onClick = onAddThoughtClick,
                    enabled = !uiState.isLoading,
                    leadingIcon = Icons.Filled.Add
                )
                CalmySecondaryButton(
                    text = "Обновить",
                    onClick = onRefreshClick,
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading,
                    leadingIcon = Icons.Filled.Refresh
                )
            }
        }
    }
}

@Composable
private fun MonthFolderCard(
    month: ThoughtMonthGroup,
    isExpanded: Boolean,
    onDeleteThoughtClick: (String) -> Unit,
    onToggle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle),
            shape = RoundedCornerShape(22.dp),
            color = CalmyColors.SurfaceSoft,
            border = BorderStroke(1.dp, CalmyColors.Stroke)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Folder,
                    contentDescription = null,
                    tint = CalmyColors.PrimaryDark
                )
                Text(
                    text = month.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = CalmyColors.TextPrimary
                )
            }
        }
        if (isExpanded) {
            month.weeks.forEach { week ->
                WeekFolderSection(
                    week = week,
                    onDeleteThoughtClick = onDeleteThoughtClick
                )
            }
        }
    }
}

@Composable
private fun WeekFolderSection(
    week: ThoughtWeekGroup,
    onDeleteThoughtClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = null,
                tint = CalmyColors.TextMuted
            )
            Text(
                text = week.title,
                style = MaterialTheme.typography.labelLarge,
                color = CalmyColors.TextSecondary
            )
        }
        week.thoughts.forEach { item ->
            ThoughtCard(
                thought = item.thought,
                formattedDate = CalmyDateTime.formatThoughtDate(item.thought.createdAt),
                onDeleteClick = { onDeleteThoughtClick(item.thought.id) }
            )
        }
    }
}

@Composable
private fun ThoughtCard(
    thought: Thought,
    formattedDate: String,
    onDeleteClick: () -> Unit
) {
    CalmyCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = thought.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CalmyColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Удалить запись",
                        tint = CalmyColors.TextMuted
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    title = "Тревога",
                    value = thought.anxietyLevel.toString(),
                    modifier = Modifier.weight(1f)
                )
                InfoChip(
                    title = "Сценарий",
                    value = thought.anxietyType.displayName,
                    modifier = Modifier.weight(1.35f)
                )
            }
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelMedium,
                color = CalmyColors.TextMuted
            )
            if (thought.isPendingSync) {
                Text(
                    text = "Ожидает отправки",
                    style = MaterialTheme.typography.labelMedium,
                    color = CalmyColors.PrimaryDark
                )
            }
        }
    }
}

private data class DatedThought(
    val thought: Thought,
    val date: java.util.Date
)

private data class ThoughtMonthGroup(
    val key: String,
    val title: String,
    val sortDate: java.util.Date,
    val weeks: List<ThoughtWeekGroup>
)

private data class ThoughtWeekGroup(
    val title: String,
    val sortDate: java.util.Date,
    val thoughts: List<DatedThought>
)

private fun groupedThoughts(thoughts: List<Thought>): List<ThoughtMonthGroup> {
    val datedThoughts = thoughts.map { thought ->
        DatedThought(
            thought = thought,
            date = CalmyDateTime.parse(thought.createdAt) ?: java.util.Date(0)
        )
    }

    return datedThoughts
        .groupBy { item -> CalmyDateTime.monthKey(item.date) }
        .map { (monthKey, monthThoughts) ->
            val monthDate = monthThoughts.maxByOrNull { item -> item.date.time }?.date ?: java.util.Date(0)
            val weeks = monthThoughts
                .groupBy { item -> CalmyDateTime.weekKey(item.date) }
                .map { (_, weekThoughts) ->
                    val weekStart = CalmyDateTime.weekStart(weekThoughts.first().date)
                    ThoughtWeekGroup(
                        title = CalmyDateTime.formatWeekFolder(weekStart),
                        sortDate = weekStart,
                        thoughts = weekThoughts.sortedByDescending { item -> item.date.time }
                    )
                }
                .sortedByDescending { week -> week.sortDate.time }
            ThoughtMonthGroup(
                key = monthKey,
                title = CalmyDateTime.formatMonthFolder(monthDate),
                sortDate = monthDate,
                weeks = weeks
            )
        }
        .sortedByDescending { month -> month.sortDate.time }
}

@Composable
private fun InfoChip(
    title: String,
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = CalmyColors.TextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = CalmyColors.TextPrimary
            )
        }
    }
}

@Composable
private fun MessageBlock(
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
