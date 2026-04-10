package com.assignment.scratchcard.data.remote.api

import kotlinx.coroutines.delay

class RetrofitActivationRemoteSourceImpl(
    private val service: RetrofitActivationService
) : ActivationRemoteSource {

    override suspend fun fetchActivationCode(code: String): Result<String> {
        return try {
            delay(2000)//added delay for easier presentation
            Result.success(service.activateCode(code).androidVersion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}