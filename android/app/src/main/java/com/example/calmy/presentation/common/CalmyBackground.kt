package com.example.calmy.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

@Composable
fun CalmyScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        CalmyColors.BackgroundPrimary,
                        CalmyColors.BackgroundSecondary,
                        CalmyColors.SecondarySoft.copy(alpha = 0.82f),
                        CalmyColors.BackgroundPrimary
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = CalmyColors.AccentSoft.copy(alpha = 0.54f),
                radius = size.minDimension * 0.38f,
                center = Offset(size.width * 0.04f, size.height * 0.12f)
            )
            drawCircle(
                color = CalmyColors.PrimaryLight.copy(alpha = 0.32f),
                radius = size.minDimension * 0.3f,
                center = Offset(size.width * 0.92f, size.height * 0.24f)
            )
            drawCircle(
                color = CalmyColors.Secondary.copy(alpha = 0.24f),
                radius = size.minDimension * 0.36f,
                center = Offset(size.width * 0.1f, size.height * 0.84f)
            )
            drawCircle(
                color = CalmyColors.Surface.copy(alpha = 0.48f),
                radius = size.minDimension * 0.22f,
                center = Offset(size.width * 0.78f, size.height * 0.76f)
            )
        }
        content()
    }
}
