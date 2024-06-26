package com.example.mobdev2.ui.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun Title(
    title: String,
    subtitle: String
) {
    Surface {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight(700)),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}