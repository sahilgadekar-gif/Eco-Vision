package com.vasundhara.app.data.remote

import com.vasundhara.app.data.local.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val prefs: UserPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { prefs.token.firstOrNull() }
        val req = if (token != null)
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        else chain.request()
        return chain.proceed(req)
    }
}
