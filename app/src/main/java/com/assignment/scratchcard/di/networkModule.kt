package com.assignment.scratchcard.di

import com.assignment.scratchcard.AppCoroutineScope
import com.assignment.scratchcard.data.remote.api.RetrofitActivationService
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun Module.networkModule() {

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // able to check URL, Query params and Response JSON
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()// For HTTP debugging
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://api.o2.sk/") // base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // create API from Retrofit
    single<RetrofitActivationService> {
        get<Retrofit>().create(RetrofitActivationService::class.java)
    }

    // 3. create global AppCoroutineScope for API calls
    single<CoroutineScope> { AppCoroutineScope.scope }
}