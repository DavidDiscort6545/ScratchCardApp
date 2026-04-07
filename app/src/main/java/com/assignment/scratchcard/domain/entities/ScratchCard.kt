package com.assignment.scratchcard.domain.entities

import androidx.compose.ui.geometry.Offset

data class ScratchCard (
    val code: String = "",
    val state: ScratchCardState = ScratchCardState.Unscratched,
    val scratchProgress: Float = 0f, // from 0.0 to 1.0
    val scratchedLines: List<List<Offset>> = emptyList()
)

enum class ScratchCardState {
    Unscratched, // untouched or not touched enough to be significant
    Scratched,   // limit has been reached (80%), code is readable now
    Activated    // activation successful via API
}