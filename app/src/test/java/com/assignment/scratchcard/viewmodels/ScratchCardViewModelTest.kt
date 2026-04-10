package com.assignment.scratchcard.viewmodels

import androidx.compose.ui.geometry.Offset
import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.domain.usecases.ActivateCardUseCase
import com.assignment.scratchcard.domain.usecases.GetScratchCardUseCase
import com.assignment.scratchcard.domain.usecases.SaveScratchCardUseCase
import com.assignment.scratchcard.domain.usecases.UpdateScratchProgressUseCase
import com.assignment.scratchcard.presentation.scratchcard.ScratchCardViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchCardViewModelTest {

    private val getScratchCardUseCase =
        mockk<GetScratchCardUseCase>(relaxed = true) // because of init block in viewmodel
    private val updateScratchProgressUseCase = mockk<UpdateScratchProgressUseCase>()
    private val saveScratchCardUseCase =
        mockk<SaveScratchCardUseCase>(relaxed = true)//relaxed because we do not need its return value
    private val activateCardUseCase = mockk<ActivateCardUseCase>()

    //lateinit because of getScratchCardUseCase() call in init block in viewmodel
    private lateinit var viewModel: ScratchCardViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // 'every' will simulate default ScratchCard get from UseCase after init
        every { getScratchCardUseCase() } returns flowOf(ScratchCard())
        viewModel = ScratchCardViewModel(
            getScratchCardUseCase,
            updateScratchProgressUseCase,
            saveScratchCardUseCase,
            activateCardUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- TESTS ---

    @Test
    fun `getInfoText returns correct string for each state`() {//runTest is not needed - function is coroutines
        Assert.assertEquals(
            "Card is activated !",
            viewModel.getInfoText(ScratchCardState.Activated)
        )
        Assert.assertEquals(
            "Card is scratched ! Activate the card!",
            viewModel.getInfoText(ScratchCardState.Scratched)
        )
        Assert.assertEquals("No card was found. Sorry !", viewModel.getInfoText(null))
    }

    @Test
    fun `updateScratchState calls save and set isUpdatingCard correctly back to false after job is done`() =
        runTest {
            // 1. GIVEN: set initial card as viewmodel starts
            val initialCard = ScratchCard(scratchedLines = emptyList())
            // setting it before creating viewmodel.
            every { getScratchCardUseCase() } returns flowOf(initialCard)

            // new VM
            viewModel = ScratchCardViewModel(
                getScratchCardUseCase,
                updateScratchProgressUseCase,
                saveScratchCardUseCase,
                activateCardUseCase
            )

            val newPoints = listOf(Offset(1f, 1f))
            val progress = 0.5f
            // processedCard will be returned after "calculations" by Use Case
            val processedCard =
                initialCard.copy(scratchProgress = progress, scratchedLines = listOf(newPoints))

            coEvery { updateScratchProgressUseCase(any(), progress) } returns processedCard
            coEvery { saveScratchCardUseCase(processedCard) } returns Unit

            // 2. WHEN
            Assert.assertFalse(viewModel.uiState.value.isUpdatingCard) //checks if isUpdatingCard is set to false before calling update
            viewModel.updateScratchState(newPoints, progress)

            // 3. THEN
            // saved should be called exactly once, and with our processedCard
            coVerify(exactly = 1) { saveScratchCardUseCase(processedCard) }

            // Check if loading finished successfully
            Assert.assertFalse(viewModel.uiState.value.isUpdatingCard)
            // we do not check the card, because viewModel does not have real repository connected
        }


}