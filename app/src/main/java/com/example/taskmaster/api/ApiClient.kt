package com.example.mykotlinclientapp.api

import com.example.taskmaster.api.AuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://your-api-url/"
    private var retrofit: Retrofit? = null
    fun create(token: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(token))
                    .build()
            )
            .build()
    }

    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("http://YOUR_SERVER_IP:3000/")  // Ganti dengan URL server Anda
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}
