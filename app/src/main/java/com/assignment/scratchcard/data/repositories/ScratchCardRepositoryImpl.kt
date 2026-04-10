package com.assignment.scratchcard.data.repositories

import com.assignment.scratchcard.data.remote.api.ActivationRemoteSource
import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class ScratchCardRepositoryImpl(
    private val api: ActivationRemoteSource,
    private val appScope: CoroutineScope // scope use for api calls
) : ScratchCardRepository {
    private val cardFlow = MutableStateFlow(
        ScratchCard(code = "Scratch to reveal the code!")
    )

    override fun getScratchCard(): Flow<ScratchCard> = cardFlow.asStateFlow()

    override suspend fun activateCard(card: ScratchCard): Result<Unit> {
        return withContext(appScope.coroutineContext) {
            try {
                val response = api.fetchActivationCode(card.code)
                val androidVersionStr = response.getOrThrow()
                val version = androidVersionStr.toIntOrNull() ?: 0

                return@withContext if (version > 277028) {
                    val updated = card.copy(state = ScratchCardState.Activated)
                    cardFlow.value = updated
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Activation code is not valid (version : $version)."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // update card from ViewModel with recompose trigger
    override suspend fun updateCard(card: ScratchCard) {
        cardFlow.value = card.copy()// copy is used to trigger recompose
    }

    override suspend fun generateRandomUUID(): String {
        delay(2000)
        return java.util.UUID.randomUUID().toString()
    }
}