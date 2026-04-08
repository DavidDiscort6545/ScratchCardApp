package com.assignment.scratchcard.presentation.scratchcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.presentation.ThemePreviews
import com.assignment.scratchcard.ui.theme.ScratchCardTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScratchCardScreen(
    isEditable: Boolean,
    viewModel: ScratchCardViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card = uiState.card

    //TODO play around with uiState variables here

    ScratchCardContent(
        card = card,
        isEditable = isEditable,
    ) { newPoints, progress ->
        viewModel.updateScratchState(newPoints, progress)
    }
}

@Composable
fun ScratchCardContent(
    card: ScratchCard?,
    isEditable: Boolean,
    onScratchStateChanged: (newPoints: List<Offset>, progress: Float) -> Unit
) {

    //TODO move here all of the stuff from mainActivity - like those 2 buttons for example

    Column(
        modifier = Modifier
            //.fillMaxSize() //TODO enable this after adding those 2 navigation buttons
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
                cardCodeDisplayText = card.code,
                isEditable = isEditable,
                onScratchStateChanged = onScratchStateChanged,
            )
            Text(text = "Scratch progress: ${(card.scratchProgress * 100).toInt()}%")
        }
    }
}

@ThemePreviews
@Composable
fun GreetingPreview() {
    ScratchCardTheme {
        ScratchCardContent(
            card = ScratchCard(
                code = "Here you will see the code after reveal",
                state = ScratchCardState.Unscratched,
            ),
            isEditable = true,
            onScratchStateChanged = { _, _ -> },
        )
    }
}