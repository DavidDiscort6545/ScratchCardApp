package com.assignment.scratchcard.presentation.composable.shared

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ButtonWithText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        enabled = enabled,
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = modifier
        )
    }
}