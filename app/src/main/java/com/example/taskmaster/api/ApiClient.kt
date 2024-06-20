package com.example.mykotlinclientapp.api

import com.example.taskmaster.api.AuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object ApiClient {
    private const val BASE_URL = "https://web-service-production-ec0e.up.railway.app/api/"
    private var retrofit: Retrofit? = null

    fun create(token: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(token))
                    .build()
            )
            .build()
    }

    fun getClient(token: String): Retrofit {
        if (retrofit == null) {
            retrofit = create(token)
        }
        return retrofit!!
    }
}
