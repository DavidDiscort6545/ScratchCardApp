package com.assignment.scratchcard.repositories

import com.assignment.scratchcard.data.remote.api.ActivationRemoteSource
import com.assignment.scratchcard.data.repositories.ScratchCardRepositoryImpl
import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchCardRepositoryTest {

    private val api = mockk<ActivationRemoteSource>()

    // We are using TestScope, for control of asynchronous operation
    private val testScope = TestScope(UnconfinedTestDispatcher())
    private val repository = ScratchCardRepositoryImpl(api, testScope)

    @Test
    fun `when api returns version above threshold, card is activated and success returned`() =
        runTest {
            // GIVEN
            val card = ScratchCard(
                code = "VALID_CODE",
                state = ScratchCardState.Scratched,
                scratchProgress = 1f
            )
            coEvery { api.fetchActivationCode("VALID_CODE") } returns "280000"

            // WHEN
            repository.updateCard(card)
            val result = repository.activateCard(card)

            // THEN
            Assert.assertTrue(result.isSuccess)
            Assert.assertEquals(
                ScratchCardState.Activated,
                repository.getScratchCard().first().state
            )
        }

    @Test
    fun `when api returns version below threshold, card remains Scratched and failure returned`() =
        runTest {
            // GIVEN
            val card = ScratchCard(
                code = "LOW_VERSION",
                state = ScratchCardState.Scratched,
                scratchProgress = 1f
            )
            coEvery { api.fetchActivationCode("LOW_VERSION") } returns "270000"

            // WHEN
            repository.updateCard(card)
            val result = repository.activateCard(card)

            // THEN
            Assert.assertTrue(result.isFailure)
            Assert.assertEquals(
                "Activation code is not valid (version : 270000).",
                result.exceptionOrNull()?.message
            )
            // Card status has to remain unchanged - Scratched  => not Activated
            Assert.assertEquals(
                ScratchCardState.Scratched,
                repository.getScratchCard().first().state
            )
        }

    @Test
    fun `when api throws exception, repository returns failure`() = runTest {
        // GIVEN
        val card = ScratchCard(code = "ERROR_CODE")
        coEvery { api.fetchActivationCode(any()) } throws Exception("Network error")

        // WHEN
        repository.updateCard(card)
        val result = repository.activateCard(card)

        // THEN
        Assert.assertTrue(result.isFailure)
        Assert.assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}