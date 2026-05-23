package com.example.calmy.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calmy.presentation.common.CalmyCard
import com.example.calmy.presentation.common.CalmyColors
import com.example.calmy.presentation.common.CalmyEmailIcon
import com.example.calmy.presentation.common.CalmyPasswordTextField
import com.example.calmy.presentation.common.CalmyPrimaryButton
import com.example.calmy.presentation.common.CalmyScreenBackground
import com.example.calmy.presentation.common.CalmySecondaryButton
import com.example.calmy.presentation.common.CalmyTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LoginScreen(
    state: StateFlow<LoginState>,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityChanged: () -> Unit,
    onLoginClick: () -> Unit,
    onOpenRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState = state.collectAsStateWithLifecycle().value
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(120)
        isContentVisible = true
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    CalmyScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            EnterBlock(visible = isContentVisible, delayMillis = 0) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Calmy",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 38.sp,
                            lineHeight = 42.sp
                        ),
                        color = CalmyColors.PrimaryDark
                    )
                    Text(
                        text = "Войди и продолжи заботиться о себе",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CalmyColors.TextSecondary
                    )
                }
            }

            EnterBlock(visible = isContentVisible, delayMillis = 100) {
                CalmyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Вход",
                            style = MaterialTheme.typography.titleMedium,
                            color = CalmyColors.TextPrimary
                        )

                        CalmyTextField(
                            label = "Email",
                            value = uiState.email,
                            onValueChange = onEmailChanged,
                            placeholder = "example@mail.com",
                            leadingIcon = CalmyEmailIcon,
                            enabled = !uiState.isLoading,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        CalmyPasswordTextField(
                            label = "Пароль",
                            value = uiState.password,
                            onValueChange = onPasswordChanged,
                            placeholder = "Пароль",
                            isVisible = uiState.isPasswordVisible,
                            onVisibilityToggle = onPasswordVisibilityChanged,
                            enabled = !uiState.isLoading
                        )

                        if (uiState.error != null) {
                            LoginErrorBlock(message = uiState.error)
                        }

                        CalmyPrimaryButton(
                            text = "Войти",
                            onClick = onLoginClick,
                            enabled = !uiState.isLoading,
                            isLoading = uiState.isLoading
                        )

                        CalmySecondaryButton(
                            text = "Перейти к регистрации",
                            onClick = onOpenRegister,
                            enabled = !uiState.isLoading
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnterBlock(
    visible: Boolean,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 650,
                delayMillis = delayMillis,
                easing = LinearOutSlowInEasing
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 750,
                delayMillis = delayMillis,
                easing = FastOutSlowInEasing
            ),
            initialOffsetY = { fullHeight -> fullHeight / 3 }
        )
    ) {
        content()
    }
}

@Composable
private fun LoginErrorBlock(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CalmyColors.Surface,
        border = BorderStroke(1.dp, CalmyColors.Stroke)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = CalmyColors.Error,
            modifier = Modifier.padding(PaddingValues(horizontal = 18.dp, vertical = 14.dp))
        )
    }
}
