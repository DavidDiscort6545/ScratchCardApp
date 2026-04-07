package com.assignment.scratchcard.presentation.scratchcard

import com.assignment.scratchcard.domain.entities.ScratchCard

data class ScratchCardUiState(
    val card: ScratchCard? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isActivating: Boolean = false,
)