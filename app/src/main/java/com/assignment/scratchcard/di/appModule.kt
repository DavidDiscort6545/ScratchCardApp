package com.assignment.scratchcard.di

import org.koin.dsl.module

val appModule = module {
    networkModule()
    dataModule()
    useCases()
    viewModelModule()
}