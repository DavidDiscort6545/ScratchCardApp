package com.assignment.scratchcard.di

import com.assignment.scratchcard.domain.usecases.ActivateCardUseCase
import com.assignment.scratchcard.domain.usecases.GetScratchCardUseCase
import com.assignment.scratchcard.domain.usecases.SaveScratchCardUseCase
import com.assignment.scratchcard.domain.usecases.UpdateScratchProgressUseCase
import org.koin.core.module.Module

fun Module.useCases() {

    factory { ActivateCardUseCase(get()) }
    factory { GetScratchCardUseCase(get()) }
    factory { SaveScratchCardUseCase(get()) }
    factory { UpdateScratchProgressUseCase(get()) }

}