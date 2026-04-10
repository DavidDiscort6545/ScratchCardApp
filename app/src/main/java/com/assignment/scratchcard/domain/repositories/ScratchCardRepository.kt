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

    /**
     * Update card with new card.
     * This will trigger recompose for the presentation layer
     * and update card stored on data layer.
     */
    suspend fun updateCard(card: ScratchCard)

    /**
     * Generated random UUID for the [ScratchCard.code].
     * This will be used for activation API as a parameter for card activation.
     */
    suspend fun generateRandomUUID(): String
}