package com.assignment.scratchcard.presentation

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light Mode",
    group = "Themes",
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    group = "Themes",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreviews