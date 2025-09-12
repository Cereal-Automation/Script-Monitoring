package com.cereal.command.monitor.data.stockx

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class OAuthInterceptor(
    private val tokenProvider: OAuthTokenProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        runBlocking {
            val token = tokenProvider.getToken()

            val newRequest =
                chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()

            chain.proceed(newRequest)
        }
}
