package com.assignment.scratchcard.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import com.assignment.scratchcard.domain.usecases.ActivateCardUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ActivateCardUseCaseTest {

    private val repository = mockk<ScratchCardRepository>()
    private val useCase = ActivateCardUseCase(repository)

    @Test
    fun `when card is Scratched, it should trigger repository activation`() = runTest {
        // GIVEN: Card is in Scratched state
        val card = ScratchCard(code = "123", state = ScratchCardState.Scratched)
        coEvery { repository.activateCard(card) } returns Result.success(Unit)

        // WHEN: call use case
        val result = useCase(card)

        // THEN: Result should be Success and repo was really called
        Assert.assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.activateCard(card) }
    }

    @Test
    fun `when card is Unscratched, it should return failure and NOT call repository`() = runTest {
        // GIVEN
        val card = ScratchCard(code = "123", state = ScratchCardState.Unscratched)

        // WHEN
        val result = useCase(card)

        // THEN: Expecting Failure with our custom message
        Assert.assertTrue(result.isFailure)
        Assert.assertEquals(
            "Card must be scratched before activation.",
            result.exceptionOrNull()?.message
        )

        // We also expect no call for activation - since we know that default card code is invalid - no need for the call to API
        coVerify(exactly = 0) { repository.activateCard(any()) }
    }

    @Test
    fun `when card is already Activated, it should return failure`() = runTest {
        // GIVEN:
        val card = ScratchCard(code = "123", state = ScratchCardState.Activated)

        // WHEN:
        val result = useCase(card)

        // THEN: Has to fail (cannot activate activated card anymore)
        Assert.assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.activateCard(any()) }
    }
}