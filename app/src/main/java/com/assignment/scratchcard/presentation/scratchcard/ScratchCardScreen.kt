package com.assignment.scratchcard.presentation.scratchcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assignment.scratchcard.domain.entities.ScratchCard
import com.assignment.scratchcard.domain.entities.ScratchCardState
import com.assignment.scratchcard.presentation.ThemePreviews
import com.assignment.scratchcard.presentation.composable.shared.ButtonWithText
import com.assignment.scratchcard.ui.theme.ScratchCardTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScratchCardScreen(
    screenTitle: String,
    isEditable: Boolean,
    isBackArrowPresent: Boolean = false,
    mode: ScreenMode,
    viewModel: ScratchCardViewModel = koinViewModel(),
    firstButtonLabel: String,
    onFirstButtonClick: () -> Unit,
    secondButtonLabel: String = "",
    onSecondNavigationButtonClick: () -> Unit,
    onBackArrowClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card = uiState.card

    if (isBackArrowPresent) {
        Row(
            modifier = Modifier.padding(8.dp),
        ) {
            IconButton(
                onClick = onBackArrowClick,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to main screen",
                )
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(viewModel.getInfoText(card?.state))
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ScratchCardContent(
                card = card,
                isEditable = isEditable,
            ) { newPoints, progress ->
                viewModel.updateScratchState(newPoints, progress)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            //first button - left one
            when (mode) {
                ScreenMode.NAVIGATION -> {
                    ButtonWithText(
                        text = firstButtonLabel,
                        modifier = Modifier.width(100.dp),
                        onClick = onFirstButtonClick
                    )
                }

                ScreenMode.SCRATCH_CARD -> {
                    ButtonWithText(
                        text = firstButtonLabel,
                        enabled = viewModel.isCardUnscratched(),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.scratchCardFully() }
                    )
                }

                ScreenMode.ACTIVATION -> {
                    ButtonWithText(
                        text = firstButtonLabel,
                        enabled = !viewModel.isCardActivated(),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.activateCard() }
                    )
                }
            }

            //second button - right one
            if (mode == ScreenMode.NAVIGATION) {
                Spacer(Modifier.weight(1f))
                ButtonWithText(
                    text = secondButtonLabel,
                    enabled = viewModel.isCardScratched(),
                    modifier = Modifier.width(100.dp),
                    onClick = onSecondNavigationButtonClick
                )
            }
        }

    }
}

/**
 * Contain Scratch Card and text below it, which describes status of the card.
 * @param card Represents card object which will be used for [ScratchCard].
 * if card is null nothing is displayed ! - future version could show some broken state.
 * @param isEditable if true the card canvas can be changed by drawing with cursor on it.
 *        if false card is not editable, but contains all scratched done previously.
 * @param onScratchStateChanged when new scratches are added to the card
 * it will be presented in "newPoint" parameter.
 * "progress" parameter represents how much percentage of the card is scratched (0f..1f) after drawing is done
 */
@Composable
fun ScratchCardContent(
    card: ScratchCard?,
    isEditable: Boolean,
    onScratchStateChanged: (newPoints: List<Offset>, progress: Float) -> Unit
) {

    Column(
        modifier = Modifier
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
                currentCardState = card.state,
                isEditable = isEditable,
                onScratchStateChanged = onScratchStateChanged,
            )
            Text(text = "Scratch progress: ${(card.scratchProgress * 100).toInt()}%")
        }
    }
}

enum class ScreenMode {
    NAVIGATION,   // navigation with left and right button
    SCRATCH_CARD, // single button with scratch action defined
    ACTIVATION    // single button with activation action defined
}

@ThemePreviews
@Composable
fun ScratchCard_unscratched_state_Preview() {
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