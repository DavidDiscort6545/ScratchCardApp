package com.assignment.scratchcard.domain.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository

/**
 * Use case for activating a scratch card.
 *
 * Ensures that a card can only be activated if it has been sufficiently
 * scratched (i.e., it is in the [ScratchCardState.Scratched] state).
 */
class ActivateCardUseCase(private val repository: ScratchCardRepository) {

    /**
     * Attempts to activate the card.
     *
     * @param card The card to activate.
     * @return [Result] of the operation. Returns failure if the card is not in a scratch able state.
     */
    suspend operator fun invoke(card: ScratchCard): Result<Unit> {
        return if (card.state == ScratchCardState.Scratched) {
            repository.activateCard(card)
        } else {
            Result.failure(IllegalStateException("Card must be scratched before activation."))
        }
    }
}