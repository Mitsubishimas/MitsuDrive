package com.mitsudrive.core.network.api

import com.mitsudrive.core.network.interceptor.AuthInterceptor
import com.mitsudrive.core.network.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    fun createOkHttpClient(
        tokenProvider: () -> String?,
        isDebug: Boolean = false
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(LoggingInterceptor.create(isDebug))
            .build()
    }
    
    fun createRetrofit(
        baseUrl: String,
        tokenProvider: () -> String?,
        isDebug: Boolean = false
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient(tokenProvider, isDebug))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    inline fun <reified T> createApi(
        baseUrl: String,
        noinline tokenProvider: () -> String?,
        isDebug: Boolean = false
    ): T {
        return createRetrofit(baseUrl, tokenProvider, isDebug).create(T::class.java)
    }
}
