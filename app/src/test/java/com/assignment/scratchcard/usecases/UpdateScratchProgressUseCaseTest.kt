package com.assignment.scratchcard.usecases

import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import com.assignment.scratchcard.domain.usecases.UpdateScratchProgressUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class UpdateScratchProgressUseCaseTest {

    private val repository = mockk<ScratchCardRepository>()
    private val useCase = UpdateScratchProgressUseCase(repository)

    @Test
    fun `when progress is less than 80 percent, state remains Unscratched`() = runTest {
        // GIVEN
        val currentCard = ScratchCard(state = ScratchCardState.Unscratched, scratchProgress = 0.0f)
        val newProgress = 0.79f

        // WHEN
        val result = useCase(currentCard, newProgress)

        // THEN
        Assert.assertEquals(ScratchCardState.Unscratched, result.state)
        Assert.assertEquals(0.79f, result.scratchProgress)
    }

    @Test
    fun `when progress reaches 80 percent, state changes to Scratched and code is generated`() =
        runTest {
            // GIVEN
            val currentCard =
                ScratchCard(state = ScratchCardState.Unscratched, scratchProgress = 0.5f)
            val newProgress = 0.8f
            val mockUuid = "generated-uuid-123"
            coEvery { repository.generateRandomUUID() } returns mockUuid

            // WHEN
            val result = useCase(currentCard, newProgress)

            // THEN
            Assert.assertEquals(ScratchCardState.Scratched, result.state)
            Assert.assertEquals(
                1.0f,
                result.scratchProgress
            ) // progress should auto finish up to 100%
            Assert.assertEquals(mockUuid, result.code)
        }

    // this case will not happen theoretically, but it is still valid case if ti happens
    @Test
    fun `when card is already Scratched, it only updates progress without re-generating code`() =
        runTest {
            // GIVEN
            val currentCard = ScratchCard(
                code = "existing-code",
                state = ScratchCardState.Scratched,
                scratchProgress = 0.9f
            )
            val newProgress = 0.95f

            // WHEN
            val result = useCase(currentCard, newProgress)

            // THEN
            Assert.assertEquals(ScratchCardState.Scratched, result.state)
            Assert.assertEquals(0.95f, result.scratchProgress)
            Assert.assertEquals("existing-code", result.code) // code should not change
        }
}