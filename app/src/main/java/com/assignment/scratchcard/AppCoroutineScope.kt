package com.assignment.scratchcard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

// Singleton, who lives during whole app
object AppCoroutineScope {
    //IO Dispatcher is used because we are expecting this scope to be used for network/data operations
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}