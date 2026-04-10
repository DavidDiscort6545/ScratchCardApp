package com.assignment.scratchcard.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import com.assignment.scratchcard.domain.usecases.GetScratchCardUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GetScratchCardUseCaseTest {

    private val repository = mockk<ScratchCardRepository>()
    private val useCase = GetScratchCardUseCase(repository)

    @Test
    fun `should emit card from repository`() = runTest {
        // GIVEN: Repo will return Flow with one card
        val expectedCard = ScratchCard(code = "TEST-123")
        every { repository.getScratchCard() } returns flowOf(expectedCard)

        // WHEN: We get just that first card
        val result = useCase().first()

        // THEN: And we should have the data here - sanity check
        Assert.assertEquals(expectedCard, result)
    }
}