package com.assignment.scratchcard.domain.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository

/**
 * Use case for persisting the current state of the scratch card.
 */
class SaveScratchCardUseCase(private val repository: ScratchCardRepository) {
    /**
     * Persists the card state to the repository.
     */
    suspend operator fun invoke(card: ScratchCard) {
        repository.updateCard(card)
    }
}