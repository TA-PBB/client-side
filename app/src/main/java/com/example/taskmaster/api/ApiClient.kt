import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://web-service-production-6319.up.railway.app/api/"

    private var retrofit: Retrofit? = null

    fun create(token: String?): Retrofit {
        if (retrofit == null) {
            val clientBuilder = OkHttpClient.Builder()
            if (token != null) {
                clientBuilder.addInterceptor(AuthInterceptor(token))
            }
            val client = clientBuilder.build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getClient(token: String): Retrofit {
        if (retrofit == null || (retrofit?.client()?.interceptors()?.none { it is AuthInterceptor } ?: true)) {
            retrofit = create(token)
        }
        return retrofit!!
    }
}
