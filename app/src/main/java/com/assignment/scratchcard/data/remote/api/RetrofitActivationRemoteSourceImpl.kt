package com.assignment.scratchcard.data.remote.api

import kotlinx.coroutines.delay

class RetrofitActivationRemoteSourceImpl(
    private val service: RetrofitActivationService
) : ActivationRemoteSource {

    override suspend fun fetchActivationCode(code: String): String {
        delay(2000)//added delay for easier presentation
        return service.activateCode(code).androidVersion
    }
}