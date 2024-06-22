package com.example.mykotlinclientapp.api

import com.example.taskmaster.api.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://web-service-production-6319.up.railway.app/api/"
    private var retrofit: Retrofit? = null

    private fun createClient(token: String?): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val builder = OkHttpClient.Builder()
            .addInterceptor(logging)

        token?.let {
            builder.addInterceptor(AuthInterceptor(it))
        }

        return builder.build()
    }

    fun create(token: String? = null): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient(token))
            .build()
    }

    fun getClient(token: String? = null): Retrofit {
        if (retrofit == null) {
            retrofit = create(token)
        }
        return retrofit!!
    }
}
