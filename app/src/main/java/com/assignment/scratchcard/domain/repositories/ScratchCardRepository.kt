package com.assignment.scratchcard.domain.repositories

import com.assignment.scratchcard.domain.entities.ScratchCard
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing scratch card data operations.
 * Defines the contract for the Data layer to provide scratch card state and activation.
 */
interface ScratchCardRepository {
    /**
     * Provides a continuous stream of the current scratch card state.
     * @return A [Flow] emitting the current [ScratchCard] entity.
     */
    fun getScratchCard(): Flow<ScratchCard>

    /**
     * Triggers the activation process of a scratched card via a remote or local service.
     *
     * @param card The card entity to be activated.
     * @return [Result] indicating success or failure of the activation process.
     */
    suspend fun activateCard(card: ScratchCard): Result<Unit>

    suspend fun updateCard(card: ScratchCard)

    suspend fun generateRandomUUID(): String
}