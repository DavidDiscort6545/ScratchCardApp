package com.assignment.scratchcard.presentation.scratchcard

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.scratchcard.domain.usecases.ActivateCardUseCase
import com.assignment.scratchcard.domain.usecases.GetScratchCardUseCase
import com.assignment.scratchcard.domain.usecases.SaveScratchCardUseCase
import com.assignment.scratchcard.domain.usecases.UpdateScratchProgressUseCase
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
                println("OBSERVE: Prišla nová karta s počtom čiar: ${card.scratchedLines.size}")
                _uiState.update { it.copy(card = card) }
            }
            .launchIn(viewModelScope)
    }

//    fun onScratchProgressChanged(progress: Float) {
//        val currentCard = _uiState.value.card ?: return
//        val updatedCard = updateScratchProgressUseCase(currentCard, progress)
//
//        // Ak by sme mali perzistenciu, tu by bol ďalší Use Case: UpdateCardUseCase(updatedCard)
//        _uiState.update { it.copy(card = updatedCard) }
//    }

    fun activateCard() {
        val currentCard = _uiState.value.card ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isActivating = true) }

            activateCardUseCase(currentCard)
                .onSuccess { _uiState.update { it.copy(isActivating = false) } }
                .onFailure { error ->
                    _uiState.update { it.copy(isActivating = false, errorMessage = error.message) }
                }
        }
    }

//    fun onNewPathCreated(newPoints: List<Offset>) {
//        val currentCard = _uiState.value.card ?: return
//
//        // Pridáme novú čiaru do zoznamu existujúcich
//        val updatedLines = currentCard.scratchedLines + listOf(newPoints)
//
//        println("VIEWMODEL: Ukladám čiaru č. ${updatedLines.size}") // SEM PRIDAJ TENTO PRINT
//
//        val updatedCard = currentCard.copy(scratchedLines = updatedLines)
//
//        //TODO Tu zatiaľ len updatneme stav, neskôr tu zavoláš UseCase na progres
//        _uiState.update { it.copy(card = updatedCard) }
//        viewModelScope.launch {
//            saveScratchCardUseCase(updatedCard)
//        }
//    }


    fun updateScratchState(newPoints: List<Offset>, progress: Float) {
        val currentCard = _uiState.value.card ?: return

        // 1. Spojíme históriu s novým ťahom
        val updatedLines = currentCard.scratchedLines + listOf(newPoints)

        // 2. Necháme UseCase vypočítať stav (percentá + Scratched stav)
        // Musíme mu poslať kartu, ktorá už má v sebe tie nové čiary!
        val cardWithLines = currentCard.copy(scratchedLines = updatedLines)
        val finalCard = updateScratchProgressUseCase(cardWithLines, progress)

        println("VIEWMODEL: Ukladám čiaru č. ${updatedLines.size}") // SEM PRIDAJ TENTO PRINT

        // 3. Uložíme do repozitára (tým sa cez Flow zaktualizuje aj UI)
        viewModelScope.launch {
            saveScratchCardUseCase(finalCard)
        }
    }
}