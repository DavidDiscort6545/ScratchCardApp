package com.assignment.scratchcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.assignment.scratchcard.presentation.scratchcard.ScratchCardScreen
import com.assignment.scratchcard.presentation.scratchcard.ScreenMode
import com.assignment.scratchcard.ui.theme.ScratchCardTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main screen with control buttons
 */
@Serializable
object MainOverviewRoute

/**
 * Screen where scratching will be done
 */
@Serializable
object ScratchCardRoute

/**
 * On this screen API can be called to activate the scratch card
 */
@Serializable
object ActivationRoute


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
                            startDestination = MainOverviewRoute,
                            modifier = Modifier.padding(innerPadding),
                        ){
                            composable<MainOverviewRoute> {

                                ScratchCardScreen(
                                    screenTitle = "Main Screen",
                                    isEditable = false,
                                    mode = ScreenMode.NAVIGATION,
                                    firstButtonLabel = "Scratch the Card here",
                                    secondButtonLabel = "Activate Card !",
                                    onFirstButtonClick = {
                                        navController.navigate(ScratchCardRoute)
                                    },
                                    onSecondNavigationButtonClick = {
                                        navController.navigate(ActivationRoute)
                                    },
                                    onBackArrowClick = { navController.popBackStack()}
                                )
                            }
                            composable<ScratchCardRoute> {

                                ScratchCardScreen(
                                    screenTitle = "Scratch Screen",
                                    isEditable = true,
                                    isBackArrowPresent = true,
                                    mode = ScreenMode.SCRATCH_CARD,
                                    firstButtonLabel = "Scratch it for me !",
                                    onFirstButtonClick = {
                                        navController.popBackStack()
                                    },
                                    onSecondNavigationButtonClick = {}, //the button will not navigate to anywhere
                                    onBackArrowClick = { navController.popBackStack()}
                                )

                            }
                            composable<ActivationRoute> {

                                ScratchCardScreen(
                                    screenTitle = "Activation Screen",
                                    isEditable = true,
                                    isBackArrowPresent = true,
                                    mode = ScreenMode.ACTIVATION,
                                    firstButtonLabel = "Activate now !",
                                    onFirstButtonClick = {
                                        navController.popBackStack()
                                    },
                                    onSecondNavigationButtonClick = {}, //the button will not navigate to anywhere
                                    onBackArrowClick = { navController.popBackStack()}
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}