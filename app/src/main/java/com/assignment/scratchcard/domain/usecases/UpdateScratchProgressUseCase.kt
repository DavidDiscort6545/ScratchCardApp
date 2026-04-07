package com.assignment.scratchcard.domain.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState

/**
 * Use case responsible for processing the scratching progress.
 *
 * It encapsulates the business rule that determines when a card transitions
 * from [ScratchCardState.Unscratched] to [ScratchCardState.Scratched] based on
 * a predefined threshold.
 */
class UpdateScratchProgressUseCase {

    /**
     * Updates the scratch card progress and evaluates its state.
     *
     * @param currentCard The current state of the card.
     * @param newProgress A float value between 0.0 and 1.0 representing the scratched area.
     * @return A new instance of [ScratchCard] with updated progress and potentially a new state.
     */
    operator fun invoke(currentCard: ScratchCard, newProgress: Float): ScratchCard {
        return if (newProgress >= 0.8f && currentCard.state == ScratchCardState.Unscratched) {
            currentCard.copy(scratchProgress = newProgress, state = ScratchCardState.Scratched)
        } else {
            currentCard.copy(scratchProgress = newProgress)
        }
    }
}