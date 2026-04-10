package com.assignment.scratchcard.domain.entities

import androidx.compose.ui.geometry.Offset

/**
 * Main data object of the project. Represents Scratch Card which stores data about it's scratches,
 * overall % progress of card reveal, [code] if card is in state [ScratchCardState.Scratched]
 */
data class ScratchCard (
    val code: String = "", // UUID if card is ScratchCardState
    val state: ScratchCardState = ScratchCardState.Unscratched, // state determines behavior of the card
    val scratchProgress: Float = 0f, // from 0.0 to 1.0
    val scratchedLines: List<List<Offset>> = emptyList()
)

enum class ScratchCardState {
    Unscratched, // untouched or not touched enough to be significant
    Scratched,   // limit has been reached (80%), code is readable now
    Activated    // activation successful via API
}