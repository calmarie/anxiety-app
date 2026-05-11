package com.example.calmy.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.calmy.presentation.common.CalmyCard
import com.example.calmy.presentation.common.CalmyScreenBackground
import com.example.calmy.presentation.common.CalmySecondaryButton

@Composable
fun HomeScreen() {
    CalmyScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Calmy",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ты успешно вошёл",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CalmyCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "☁️ Добро пожаловать",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Твои мысли теперь в спокойном и аккуратном пространстве. Дальше можно быстро записать важное или открыть сохранённые записи.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HomeActionCard(
                title = "Записать мысль",
                icon = Icons.Filled.Edit
            )

            HomeActionCard(
                title = "Мои записи",
                icon = Icons.Filled.Home
            )

            HomeActionCard(
                title = "Настройки",
                icon = Icons.Filled.Spa
            )
        }
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    icon: ImageVector
) {
    CalmyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                CalmySecondaryButton(
                    text = "Скоро доступно",
                    onClick = {}
                )
            }
            Text(
                text = when (icon) {
                    Icons.Filled.Edit -> "✍️"
                    Icons.Filled.Home -> "📚"
                    else -> "⚙️"
                },
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
