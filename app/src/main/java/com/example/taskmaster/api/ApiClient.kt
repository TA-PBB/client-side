package com.example.taskmaster.api

import AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://web-service-production-6319.up.railway.app/api/"

    fun getClient(token: String?): Retrofit {
        val clientBuilder = OkHttpClient.Builder()
        if (token != null) {
            clientBuilder.addInterceptor(AuthInterceptor(token))
        }
        val client = clientBuilder.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
