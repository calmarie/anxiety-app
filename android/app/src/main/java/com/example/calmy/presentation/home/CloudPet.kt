package com.example.calmy.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.calmy.R

@Composable
fun CloudPet(
    level: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = cloudImageRes(level)),
        contentDescription = "Тучка настроения",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

private fun cloudImageRes(level: Int): Int {
    return when (level.coerceIn(1, 5)) {
        1 -> R.drawable.cloud_lv1
        2 -> R.drawable.cloud_lv2
        3 -> R.drawable.cloud_lv3
        4 -> R.drawable.cloud_lv4
        else -> R.drawable.cloud_lv5
    }
}
