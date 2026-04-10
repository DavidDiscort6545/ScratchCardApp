package com.assignment.scratchcard.di

import com.assignment.scratchcard.data.remote.api.ActivationRemoteSource
import com.assignment.scratchcard.data.remote.api.RetrofitActivationRemoteSourceImpl
import com.assignment.scratchcard.data.repositories.ScratchCardRepositoryImpl
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import org.koin.core.module.Module

fun Module.dataModule() {

    single<ActivationRemoteSource> { RetrofitActivationRemoteSourceImpl(get()) }

    single<ScratchCardRepository> { ScratchCardRepositoryImpl(get(),get()) }
}