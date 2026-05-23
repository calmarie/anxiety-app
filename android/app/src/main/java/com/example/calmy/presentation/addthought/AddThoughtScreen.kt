package com.example.calmy.presentation.addthought

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calmy.domain.model.AnxietyType
import com.example.calmy.presentation.common.CalmyBottomNavDestination
import com.example.calmy.presentation.common.CalmyBottomNavigation
import com.example.calmy.presentation.common.CalmyCard
import com.example.calmy.presentation.common.CalmyColors
import com.example.calmy.presentation.common.CalmyPrimaryButton
import com.example.calmy.presentation.common.CalmyScreenBackground
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AddThoughtScreen(
    state: StateFlow<AddThoughtState>,
    onDescriptionChanged: (String) -> Unit,
    onAnxietyLevelSelected: (Int) -> Unit,
    onAnxietyTypeSelected: (AnxietyType) -> Unit,
    onSaveClick: () -> Unit,
    onSaved: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenAddThought: () -> Unit,
    onOpenThoughtList: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState = state.collectAsStateWithLifecycle().value

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaved()
        }
    }

    CalmyScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CalmyBottomNavigation(
                    selectedDestination = CalmyBottomNavDestination.AddThought,
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
                        text = "Записать мысль",
                        style = MaterialTheme.typography.displaySmall,
                        color = CalmyColors.PrimaryDark
                    )
                    Text(
                        text = "Выбери сценарий, уровень и сохрани запись",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CalmyColors.TextSecondary
                    )
                }

                CalmyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        DescriptionField(
                            value = uiState.description,
                            enabled = !uiState.isSaving,
                            onValueChange = onDescriptionChanged
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Сценарий тревоги",
                                style = MaterialTheme.typography.titleMedium,
                                color = CalmyColors.TextPrimary
                            )
                            AnxietyTypeSelector(
                                selected = uiState.anxietyType,
                                enabled = !uiState.isSaving,
                                onSelect = onAnxietyTypeSelected
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Уровень тревоги: ${uiState.anxietyLevel}",
                                style = MaterialTheme.typography.titleMedium,
                                color = CalmyColors.TextPrimary
                            )
                            AnxietyLevelSelector(
                                selected = uiState.anxietyLevel,
                                enabled = !uiState.isSaving,
                                onSelect = onAnxietyLevelSelected
                            )
                        }
                        if (uiState.error != null) {
                            MessageBlock(
                                text = uiState.error,
                                color = CalmyColors.Error
                            )
                        }
                        CalmyPrimaryButton(
                            text = "Сохранить",
                            onClick = onSaveClick,
                            enabled = !uiState.isSaving,
                            isLoading = uiState.isSaving,
                            leadingIcon = Icons.Filled.Save
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DescriptionField(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Что сейчас в голове?",
            style = MaterialTheme.typography.labelMedium,
            color = CalmyColors.TextSecondary
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 144.dp),
            enabled = enabled,
            shape = RoundedCornerShape(22.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = CalmyColors.TextPrimary),
            placeholder = {
                Text(
                    text = "Можно написать одной фразой",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CalmyColors.TextMuted
                )
            },
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

@Composable
private fun AnxietyTypeSelector(
    selected: AnxietyType,
    enabled: Boolean,
    onSelect: (AnxietyType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AnxietyType.selectableValues.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { type ->
                    SelectableChip(
                        text = type.displayName,
                        selected = selected == type,
                        enabled = enabled,
                        onClick = { onSelect(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }
        }
    }
}

@Composable
private fun AnxietyLevelSelector(
    selected: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(1..5, 6..10).forEach { range ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                range.forEach { value ->
                    SelectableChip(
                        text = value.toString(),
                        selected = selected == value,
                        enabled = enabled,
                        onClick = { onSelect(value) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectableChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = if (selected) CalmyColors.PrimaryLight else CalmyColors.Surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) CalmyColors.Primary else CalmyColors.Stroke
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = CalmyColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun MessageBlock(
    text: String,
    color: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CalmyColors.SurfaceSoft,
        border = BorderStroke(1.dp, CalmyColors.Stroke)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.padding(PaddingValues(horizontal = 18.dp, vertical = 14.dp))
        )
    }
}
