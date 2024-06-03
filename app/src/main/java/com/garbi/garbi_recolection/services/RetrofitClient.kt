package com.garbi.garbi_recolection.services
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
    private const val BASE_URL = "http://54.152.182.89"
    private var token: String? = null

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("accept", "application/json")

                token?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    val containerService: ContainerService by lazy {
        retrofit.create(ContainerService::class.java)
    }

    val loginService: LoginService by lazy {
        retrofit.create(LoginService::class.java)
    }

    fun setToken(token: String){
        this.token = token;
    }
}