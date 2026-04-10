package com.assignment.scratchcard.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import com.assignment.scratchcard.domain.usecases.SaveScratchCardUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveScratchCardUseCaseTest {

    private val repository = mockk<ScratchCardRepository>()
    private val useCase = SaveScratchCardUseCase(repository)

    @Test
    fun `should call repository update when invoked`() = runTest {
        // GIVEN
        val card = ScratchCard(code = "SAVE-TEST", scratchProgress = 0.5f)
        coEvery { repository.updateCard(any()) } returns Unit

        // WHEN
        useCase(card)

        // THEN: check if pass-through works correctly via Use Case
        coVerify(exactly = 1) { repository.updateCard(card) }
    }
}