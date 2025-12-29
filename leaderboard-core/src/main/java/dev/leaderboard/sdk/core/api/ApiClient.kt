package dev.leaderboard.sdk.core.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var retrofit: Retrofit? = null

    fun <T> create(baseUrl: String, serviceClass: Class<T>): T {
        if (retrofit == null || retrofit?.baseUrl().toString() != baseUrl) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(serviceClass)
    }
}
