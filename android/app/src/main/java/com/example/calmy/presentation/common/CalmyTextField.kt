package com.example.calmy.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CalmyTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Filled.Person,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = CalmyColors.TextSecondary
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = CalmyColors.TextPrimary),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CalmyColors.TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = CalmyColors.TextMuted
                )
            },
            keyboardOptions = keyboardOptions,
            colors = calmyTextFieldColors()
        )
    }
}

@Composable
fun CalmyPasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = CalmyColors.TextSecondary
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = CalmyColors.TextPrimary),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CalmyColors.TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = CalmyColors.TextMuted
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onVisibilityToggle,
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = CalmyColors.TextMuted
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = calmyTextFieldColors()
        )
    }
}

@Composable
private fun calmyTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CalmyColors.Surface,
    unfocusedContainerColor = CalmyColors.Surface,
    disabledContainerColor = CalmyColors.Surface,
    errorContainerColor = CalmyColors.Surface,
    focusedBorderColor = CalmyColors.Primary,
    unfocusedBorderColor = CalmyColors.Stroke,
    disabledBorderColor = CalmyColors.Stroke,
    errorBorderColor = CalmyColors.Error,
    focusedTextColor = CalmyColors.TextPrimary,
    unfocusedTextColor = CalmyColors.TextPrimary,
    disabledTextColor = CalmyColors.TextMuted,
    cursorColor = CalmyColors.Primary,
    focusedLeadingIconColor = CalmyColors.Primary,
    unfocusedLeadingIconColor = CalmyColors.TextMuted,
    focusedTrailingIconColor = CalmyColors.Primary,
    unfocusedTrailingIconColor = CalmyColors.TextMuted
)

val CalmyNameIcon: ImageVector
    get() = Icons.Filled.Person

val CalmyEmailIcon: ImageVector
    get() = Icons.Filled.Email
