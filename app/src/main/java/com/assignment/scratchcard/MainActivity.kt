package com.assignment.scratchcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.assignment.scratchcard.presentation.ThemePreviews
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
                                // Vytvoríme inštanciu faku (zatiaľ ručne, kým nemáš Koin)
                                //val scratchCardRepository = remember { FakeScratchCardRepository() }//TODO use KOIN

//                                val scratchCardViewModel = remember {
//                                    ScratchCardViewModel(
//                                        getScratchCardUseCase = GetScratchCardUseCase(scratchCardRepository),
//                                        activateCardUseCase = ActivateCardUseCase(scratchCardRepository),
//                                        saveScratchCardUseCase = SaveScratchCardUseCase(scratchCardRepository),
//                                        updateScratchProgressUseCase = UpdateScratchProgressUseCase()
//                                    )
//                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ScratchCardScreen()
                                }
                            }
                            composable<ScratchCardRoute> {
                            //TODO

//                                    ButtonWithText(
//                                        text = "ScratchCardRoute",
//                                        onClick = {
//                                            navController.navigate(ScratchCardRoute)
//                                        }
//                                    )
//                                    ButtonWithText(
//                                        text = "ActivationRoute",
//                                        onClick = {
//                                            navController.navigate(ActivationRoute(null))
//                                        }
//                                    )
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

@Deprecated("used as default component, if not needed just remove it")
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@ThemePreviews
@Composable
fun GreetingPreview() {
    ScratchCardTheme {
        Greeting("Android")
    }
}