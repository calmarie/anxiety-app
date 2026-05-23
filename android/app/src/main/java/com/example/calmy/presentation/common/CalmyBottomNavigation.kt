package com.example.calmy.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class CalmyBottomNavDestination {
    Home,
    AddThought,
    ThoughtList,
    Statistics,
    Settings
}

@Composable
fun CalmyBottomNavigation(
    selectedDestination: CalmyBottomNavDestination,
    onHomeClick: () -> Unit,
    onAddThoughtClick: () -> Unit,
    onThoughtListClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        CalmyBottomNavItem(
            destination = CalmyBottomNavDestination.Home,
            label = "Главная",
            icon = Icons.Filled.Home,
            onClick = onHomeClick
        ),
        CalmyBottomNavItem(
            destination = CalmyBottomNavDestination.AddThought,
            label = "Мысль",
            icon = Icons.Filled.Edit,
            onClick = onAddThoughtClick
        ),
        CalmyBottomNavItem(
            destination = CalmyBottomNavDestination.ThoughtList,
            label = "Записи",
            icon = Icons.AutoMirrored.Filled.List,
            onClick = onThoughtListClick
        ),
        CalmyBottomNavItem(
            destination = CalmyBottomNavDestination.Statistics,
            label = "Стат.",
            icon = Icons.Filled.BarChart,
            onClick = onStatisticsClick
        ),
        CalmyBottomNavItem(
            destination = CalmyBottomNavDestination.Settings,
            label = "Настр.",
            icon = Icons.Filled.Settings,
            onClick = onSettingsClick
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = CalmyColors.Surface.copy(alpha = 0.98f),
        contentColor = CalmyColors.TextPrimary,
        border = BorderStroke(1.dp, CalmyColors.Stroke),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = CalmyColors.TextPrimary,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                val selected = selectedDestination == item.destination
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            item.onClick()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CalmyColors.PrimaryDark,
                        selectedTextColor = CalmyColors.PrimaryDark,
                        indicatorColor = CalmyColors.PrimaryLight.copy(alpha = 0.74f),
                        unselectedIconColor = CalmyColors.TextMuted,
                        unselectedTextColor = CalmyColors.TextMuted
                    )
                )
            }
        }
    }
}

private data class CalmyBottomNavItem(
    val destination: CalmyBottomNavDestination,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
