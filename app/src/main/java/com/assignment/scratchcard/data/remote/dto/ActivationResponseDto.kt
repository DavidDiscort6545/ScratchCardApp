package com.assignment.scratchcard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ActivationResponseDto(
    @SerializedName("android") val androidVersion: String
)