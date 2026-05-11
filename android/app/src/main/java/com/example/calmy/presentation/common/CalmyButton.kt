package com.example.calmy.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CalmyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CalmyColors.Primary,
            contentColor = CalmyColors.Surface,
            disabledContainerColor = CalmyColors.Disabled,
            disabledContentColor = CalmyColors.Surface
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = CalmyColors.Surface,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun CalmySecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CalmyColors.Stroke),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CalmyColors.SurfaceSoft,
            contentColor = CalmyColors.TextPrimary,
            disabledContainerColor = CalmyColors.SurfaceSoft,
            disabledContentColor = CalmyColors.TextMuted
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Unspecified
        )
    }
}
