package com.assignment.scratchcard.presentation.scratchcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScratchCardScreen(
    viewModel: ScratchCardViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card = uiState.card

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (card != null) {
            ScratchCardCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                lines = card.scratchedLines, //send saved lines
                isEditable = true,
                onScratchStateChanged = { newPoints, progress ->
                    viewModel.updateScratchState(newPoints, progress)
                },
//                onNewPathCreated = { newPoints ->
//                    viewModel.onNewPathCreated(newPoints)
//                },
//                onProgressChanged = { progress ->
//                    viewModel.onScratchProgressChanged(progress)
//                    viewModel.updateScratchState()
//                },

            )
            Text(text = "Scratch progress: ${(card.scratchProgress * 100).toInt()}%")
        }
    }
}

//TODO add preview