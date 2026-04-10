package com.assignment.scratchcard.domain.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case responsible for processing the scratching progress.
 *
 * It encapsulates the business rule that determines when a card transitions
 * from [ScratchCardState.Unscratched] to [ScratchCardState.Scratched] based on
 * a predefined threshold.
 */
class UpdateScratchProgressUseCase(private val repository: ScratchCardRepository) {

    /**
     * Updates the scratch card progress and evaluates its state.
     *
     * @param currentCard The current state of the card.
     * @param newProgress A float value between 0.0 and 1.0 representing the scratched area.
     * @return A new instance of [ScratchCard] with updated progress and potentially a new state.
     */
    suspend operator fun invoke(currentCard: ScratchCard, newProgress: Float): ScratchCard = withContext(Dispatchers.Default) {
        if (newProgress >= 0.8f && currentCard.state == ScratchCardState.Unscratched) {
            currentCard.copy(
                code = repository.generateRandomUUID(),
                scratchProgress = 1f,// we will immediately finish scratching for the user
                state = ScratchCardState.Scratched)
        } else {
            currentCard.copy(scratchProgress = newProgress)
        }
    }
}