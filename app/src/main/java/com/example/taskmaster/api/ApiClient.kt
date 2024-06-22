package com.example.mykotlinclientapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://web-service-production-6319.up.railway.app/api/"
    private var retrofit: Retrofit? = null

    private fun createClient(token: String? = null): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val builder = OkHttpClient.Builder()
            .addInterceptor(logging)

        token?.let {
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer <your-token>")
                    .build()
                chain.proceed(request)
            }
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
        if (retrofit == null || token != null) {  // Reinitialize if a token is provided
            retrofit = create(token)
        }
        return retrofit!!
    }
}
