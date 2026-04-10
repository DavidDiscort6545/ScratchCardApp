package com.assignment.scratchcard.data.remote.api

interface ActivationRemoteSource {
    suspend fun fetchActivationCode(code: String): String
}