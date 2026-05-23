package com.example.calmy.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calmy.presentation.common.CalmyBottomNavDestination
import com.example.calmy.presentation.common.CalmyBottomNavigation
import com.example.calmy.presentation.common.CalmyCard
import com.example.calmy.presentation.common.CalmyColors
import com.example.calmy.presentation.common.CalmyScreenBackground
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(
    state: StateFlow<HomeState>,
    onOpenAddThought: () -> Unit,
    onOpenThoughtList: () -> Unit,
    onCheckStatistics: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit,
    onStatisticsNavigationConsumed: () -> Unit
) {
    val uiState = state.collectAsStateWithLifecycle().value

    LaunchedEffect(uiState.openStatistics) {
        if (uiState.openStatistics) {
            onOpenStatistics()
            onStatisticsNavigationConsumed()
        }
    }

    CalmyScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CalmyBottomNavigation(
                    selectedDestination = CalmyBottomNavDestination.Home,
                    onHomeClick = {},
                    onAddThoughtClick = onOpenAddThought,
                    onThoughtListClick = onOpenThoughtList,
                    onStatisticsClick = onCheckStatistics,
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
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = greeting(uiState.userName),
                        style = MaterialTheme.typography.displaySmall,
                        color = CalmyColors.PrimaryDark
                    )
                    Text(
                        text = "Тучка отражает настроение недели",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CalmyColors.TextSecondary
                    )
                }

                CalmyCard(
                    useSoftSurface = true,
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        CloudPet(
                            level = uiState.cloudLevel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(204.dp)
                        )
                        Text(
                            text = cloudMoodText(uiState.cloudLevel),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CalmyColors.TextSecondary
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
                            color = CalmyColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

private fun greeting(name: String): String {
    return if (name.isBlank()) {
        "Привет"
    } else {
        "Привет, ${name.trim()}"
    }
}

private fun cloudMoodText(level: Int): String {
    return when (level.coerceIn(1, 5)) {
        1 -> "Сейчас тучка радуется: отдохните, вы справляетесь, всё хорошо"
        2 -> "Сейчас тучка улыбается: сделайте спокойный вдох и побудьте в тишине пару минут"
        3 -> "Сейчас тучка задумалась: остановитесь, выпейте воды и напомните себе, что вы в безопасности"
        4 -> "Сейчас тучка устала: дайте себе паузу, расслабьте плечи и не требуйте от себя слишком многого"
        else -> "Сейчас тучке тяжело: пожалуйста, замедлитесь, подышите ровно и попросите поддержки, если она нужна"
    }
}
