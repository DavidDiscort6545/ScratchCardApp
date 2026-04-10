package com.assignment.scratchcard.presentation.scratchcard

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.domain.usecases.ActivateCardUseCase
import com.assignment.scratchcard.domain.usecases.GetScratchCardUseCase
import com.assignment.scratchcard.domain.usecases.SaveScratchCardUseCase
import com.assignment.scratchcard.domain.usecases.UpdateScratchProgressUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScratchCardViewModel(
    private val getScratchCardUseCase: GetScratchCardUseCase,
    private val updateScratchProgressUseCase: UpdateScratchProgressUseCase,
    private val saveScratchCardUseCase: SaveScratchCardUseCase,
    private val activateCardUseCase: ActivateCardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScratchCardUiState())
    val uiState: StateFlow<ScratchCardUiState> = _uiState.asStateFlow()

    init {
        observeCard()
    }

    private fun observeCard() {
        getScratchCardUseCase()
            .onEach { card ->
                _uiState.update { it.copy(card = card) }
            }
            .launchIn(viewModelScope)
    }

    fun activateCard() {
        val currentCard = _uiState.value.card ?: return
        if (uiState.value.isActivating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isActivating = true) }

            activateCardUseCase(currentCard)
                .onSuccess { _uiState.update { it.copy(isActivating = false) } }
                .onFailure { error ->
                    _uiState.update { it.copy(isActivating = false, errorMessage = error.message) }
                }
        }
    }

    fun getInfoText(cardState:ScratchCardState?):String {
        return when (cardState){
            ScratchCardState.Unscratched -> "Activate the card, after scratching it"
            ScratchCardState.Scratched -> "Card is scratched ! Activate the card!"
            ScratchCardState.Activated -> "Card is activated !"
            null -> "No card was found. Sorry !"
        }
    }

    fun isCardUnscratched(): Boolean {
        val currentCard = _uiState.value.card ?: return false
        return currentCard.state == ScratchCardState.Unscratched
    }

    fun isCardScratched(): Boolean {
        val currentCard = _uiState.value.card ?: return false
        return currentCard.state == ScratchCardState.Scratched
    }

    fun isCardActivated(): Boolean {
        val currentCard = _uiState.value.card ?: return false
        return currentCard.state == ScratchCardState.Activated
    }

    fun isCardFullyRevealed(): Boolean {
        val currentCard = _uiState.value.card ?: return false
        return currentCard.scratchProgress > 0.95f
    }

    fun updateScratchState(newPoints: List<Offset>, progress: Float) {
        val currentCard = _uiState.value.card ?: return

        // 1. Join old scratches with new swipe
        val updatedLines = currentCard.scratchedLines + listOf(newPoints)

        // Musíme mu poslať kartu, ktorá už má v sebe tie nové čiary!
        val cardWithLines = currentCard.copy(scratchedLines = updatedLines)

        // since we have suspend function in this function we want to avoid running it multiple times
        if (uiState.value.isUpdatingCard) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true) }
            try {
                // 2. Necháme UseCase vypočítať stav (percentá + Scratched stav)
                val finalCard = updateScratchProgressUseCase(cardWithLines, progress)

                // 3. Uložíme do repozitára (tým sa cez Flow zaktualizuje aj UI)
                saveScratchCardUseCase(finalCard)
            }catch (e :CancellationException) {
                println("Updating Scratch card with UUID was canceled unexpectedly! Try again !")
                throw e
            } finally {
                _uiState.update { it.copy(isUpdatingCard = false) }
            }

        }
    }

    fun scratchCard() {
        updateScratchState(
            newPoints = listOf(),//TODO we want to have reverse here ... all the points ...
            progress = 1f
        )
    }
}