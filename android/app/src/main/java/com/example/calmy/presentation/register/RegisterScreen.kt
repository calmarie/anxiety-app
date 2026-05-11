package com.example.calmy.presentation.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calmy.presentation.common.CalmyCard
import com.example.calmy.presentation.common.CalmyColors
import com.example.calmy.presentation.common.CalmyEmailIcon
import com.example.calmy.presentation.common.CalmyNameIcon
import com.example.calmy.presentation.common.CalmyPasswordTextField
import com.example.calmy.presentation.common.CalmyPrimaryButton
import com.example.calmy.presentation.common.CalmyScreenBackground
import com.example.calmy.presentation.common.CalmyTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RegisterScreen(
    state: StateFlow<RegisterState>,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityChanged: () -> Unit,
    onRegisterClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState = state.collectAsStateWithLifecycle().value
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(120)
        isContentVisible = true
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    CalmyScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            EnterAnimationBlock(
                visible = isContentVisible,
                delayMillis = 0
            ) {
                DecorativeHeader()
            }

            EnterAnimationBlock(
                visible = isContentVisible,
                delayMillis = 100
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                        text = "Создай аккаунт и почувствуй заботу",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CalmyColors.TextSecondary
                    )
                }
            }

            EnterAnimationBlock(
                visible = isContentVisible,
                delayMillis = 180
            ) {
                CalmyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        CalmyTextField(
                            label = "Имя",
                            value = uiState.name,
                            onValueChange = onNameChanged,
                            placeholder = "Как к тебе обращаться?",
                            leadingIcon = CalmyNameIcon,
                            enabled = !uiState.isLoading
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
                            placeholder = "Минимум 6 символов",
                            isVisible = uiState.isPasswordVisible,
                            onVisibilityToggle = onPasswordVisibilityChanged,
                            enabled = !uiState.isLoading
                        )

                        if (uiState.error != null) {
                            ErrorBlock(message = uiState.error)
                        }

                        CalmyPrimaryButton(
                            text = "Зарегистрироваться",
                            onClick = onRegisterClick,
                            enabled = !uiState.isLoading,
                            isLoading = uiState.isLoading
                        )
                    }
                }
            }

            EnterAnimationBlock(
                visible = isContentVisible,
                delayMillis = 260
            ) {
                Text(
                    text = "Твои записи будут доступны только тебе",
                    style = MaterialTheme.typography.labelMedium,
                    color = CalmyColors.TextMuted,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EnterAnimationBlock(
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
private fun DecorativeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        )
    }
}

@Composable
private fun ErrorBlock(message: String) {
    CalmyCard(
        useSoftSurface = true,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = "♡ $message",
            style = MaterialTheme.typography.bodyMedium,
            color = CalmyColors.Error
        )
    }
}
