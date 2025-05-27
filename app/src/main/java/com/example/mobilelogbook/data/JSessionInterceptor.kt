package com.example.mobilelogbook.data

import com.example.mobilelogbook.session.UserSession
import okhttp3.Interceptor
import okhttp3.Response

class JSessionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val sessionId = UserSession.getSessionId()

        return if (!sessionId.isNullOrEmpty()) {
            val modifiedRequest = originalRequest.newBuilder()
                .addHeader("Cookie", "JSESSIONID=$sessionId")
                .build()
            chain.proceed(modifiedRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
