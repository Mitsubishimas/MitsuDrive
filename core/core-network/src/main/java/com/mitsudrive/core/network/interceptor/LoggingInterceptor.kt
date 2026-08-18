package com.mitsudrive.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

object LoggingInterceptor {
    fun create(isDebug: Boolean = false): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
    }
}
