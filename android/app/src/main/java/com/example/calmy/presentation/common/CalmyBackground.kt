package com.example.calmy.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Fill

@Composable
fun CalmyScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CalmyColors.BackgroundPrimary)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = CalmyColors.PrimaryLight.copy(alpha = 0.55f),
                radius = size.minDimension * 0.22f,
                center = Offset(size.width * 0.85f, size.height * 0.12f)
            )
            drawCircle(
                color = CalmyColors.SecondarySoft.copy(alpha = 0.85f),
                radius = size.minDimension * 0.18f,
                center = Offset(size.width * 0.16f, size.height * 0.2f)
            )
            drawOval(
                color = CalmyColors.AccentSoft.copy(alpha = 0.7f),
                topLeft = Offset(size.width * 0.62f, size.height * 0.68f),
                size = Size(size.width * 0.3f, size.height * 0.12f),
                style = Fill
            )
            drawOval(
                color = CalmyColors.BackgroundSecondary.copy(alpha = 0.9f),
                topLeft = Offset(size.width * -0.02f, size.height * 0.72f),
                size = Size(size.width * 0.34f, size.height * 0.14f),
                style = Fill
            )
        }
        content()
    }
}
