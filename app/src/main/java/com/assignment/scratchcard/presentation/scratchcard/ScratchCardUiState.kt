package com.assignment.scratchcard.presentation.scratchcard

import com.assignment.scratchcard.domain.entities.ScratchCard

data class ScratchCardUiState(
    val card: ScratchCard? = null, // main object containing all important data about the card
    val isLoading: Boolean = false, // can be used later for loading states
    val errorMessage: String? = null,
    val isUpdatingCard: Boolean = false, // practically, mutex flag while card is updating
    val isActivating: Boolean = false, // status when we are waiting for BE to respond with activation status
)