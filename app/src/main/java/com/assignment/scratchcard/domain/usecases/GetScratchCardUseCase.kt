package com.assignment.scratchcard.domain.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to observe the current state of the scratch card.
 */
class GetScratchCardUseCase(private val repository: ScratchCardRepository) {

    /**
     * @return A [Flow] of the [ScratchCard] entity.
     */
    operator fun invoke(): Flow<ScratchCard> = repository.getScratchCard()
}