package com.example.calmy.presentation.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calmy.presentation.common.CalmyColors
import com.example.calmy.presentation.common.CalmyScreenBackground
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SplashScreen(
    state: StateFlow<SplashState>,
    onOpenHome: () -> Unit,
    onOpenLogin: () -> Unit
) {
    val uiState = state.collectAsStateWithLifecycle().value

    LaunchedEffect(uiState.destination) {
        when (uiState.destination) {
            SplashDestination.Home -> onOpenHome()
            SplashDestination.Login -> onOpenLogin()
            null -> Unit
        }
    }

    CalmyScreenBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Calmy",
                style = MaterialTheme.typography.displaySmall,
                color = CalmyColors.PrimaryDark
            )
            CircularProgressIndicator(
                color = CalmyColors.Primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
