package com.garbi.garbi_recolection.services
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
    private const val BASE_URL = "http://54.152.182.89"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val containerService: ContainerService by lazy {
        retrofit.create(ContainerService::class.java)
    }
}