package com.assignment.scratchcard.data.remote.api

import com.assignment.scratchcard.data.remote.dto.ActivationResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit API
interface RetrofitActivationService {
    @GET("version")
    suspend fun activateCode(
        @Query("code") code: String
    ): ActivationResponseDto
}