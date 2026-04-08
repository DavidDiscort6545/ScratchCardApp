package com.assignment.scratchcard.data.repositories

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeScratchCardRepository : ScratchCardRepository {
    private val cardFlow = MutableStateFlow(
        ScratchCard(code = "Scratch to reveal the code!")
    )

    override fun getScratchCard(): Flow<ScratchCard> = cardFlow.asStateFlow()

    override suspend fun activateCard(card: ScratchCard): Result<Unit> {
        // simulate network delay
        delay(2000)
        val updated = card.copy(state = ScratchCardState.Activated)
        cardFlow.value = updated
        return Result.success(Unit)
    }

    // update card from ViewModel with recompose trigger
    override suspend fun updateCard(card: ScratchCard) {
        cardFlow.value = card.copy()// copy is used to trigger recompose
    }
}