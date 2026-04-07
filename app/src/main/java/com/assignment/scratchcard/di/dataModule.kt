package com.assignment.scratchcard.di

import com.assignment.scratchcard.data.repositories.FakeScratchCardRepository
import com.assignment.scratchcard.domain.repositories.ScratchCardRepository
import org.koin.core.module.Module

fun Module.dataModule() {

    single<ScratchCardRepository> { FakeScratchCardRepository() }
}