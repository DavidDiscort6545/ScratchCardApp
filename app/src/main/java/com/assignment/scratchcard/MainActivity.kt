package com.assignment.scratchcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.assignment.scratchcard.presentation.ThemePreviews
import com.assignment.scratchcard.presentation.composable.shared.ButtonWithText
import com.assignment.scratchcard.presentation.scratchcard.ScratchCardScreen
import com.assignment.scratchcard.ui.theme.ScratchCardTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main screen with control buttons
 * @param scratchCardCodeValue if value is provided this value will be displayed instead of Scratch Card
 * If null is provided, Scratch Card will be visible without clear code.
 */
@Serializable
data class MainOverviewRoute(val scratchCardCodeValue: String?)

/**
 * Screen where scratching will be done
 */
@Serializable
object ScratchCardRoute

/**
 * On this screen API can be called to activate the scratch card
 * @param revealedCode code that will be sent to API.
 * If [revealedCode] is null, sending will not be possible.
 */
@Serializable
data class ActivationRoute(val revealedCode: String?)//if null is provided no code was generated yet


class MainActivity : ComponentActivity() {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KoinAndroidContext {
                ScratchCardTheme {
                    val navController = rememberNavController()

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavHost(
                            navController = navController,
                            // This tab will be first to appear, null value meas that card code is still not known
                            startDestination = MainOverviewRoute(null),
                            modifier = Modifier.padding(innerPadding),
                        ){
                            composable<MainOverviewRoute> {

                                //TODO move these columns are rows to the screen itself and here just provide the data for the screen

                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        ScratchCardScreen(false)
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .align(Alignment.CenterHorizontally)

                                    ) {
                                        ButtonWithText(
                                            text = "Scratch the Card here",
                                            modifier = Modifier.width(100.dp),
                                            onClick = {
                                                navController.navigate(ScratchCardRoute)
                                            }
                                        )
                                        Spacer(Modifier.weight(1f))
                                        ButtonWithText(
                                            text = "Activate Card !",
                                            enabled = false,
                                            modifier = Modifier.width(100.dp),
                                            onClick = {
                                                navController.navigate(ActivationRoute(null))
                                            }
                                        )
                                    }

                                }
                            }
                            composable<ScratchCardRoute> {
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        ScratchCardScreen(true)
                                    }
                                }

                            }
                            composable<ActivationRoute> {
                            //TODO
                            }
                        }

                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun GreetingPreview() {
    ScratchCardTheme {

    }
}