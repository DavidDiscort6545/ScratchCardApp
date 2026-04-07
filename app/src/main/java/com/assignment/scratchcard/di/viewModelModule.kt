package com.assignment.scratchcard.di

import com.assignment.scratchcard.presentation.scratchcard.ScratchCardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module

fun Module.viewModelModule() {

    viewModel { ScratchCardViewModel(get(), get(), get(), get()) }

}