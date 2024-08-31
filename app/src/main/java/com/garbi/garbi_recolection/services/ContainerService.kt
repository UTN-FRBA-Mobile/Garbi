package com.garbi.garbi_recolection.services

import ContainerResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.Response

interface ContainerService {
    @GET("/integration/container")
    @Headers("accept: application/json")
    suspend fun getContainers(): Response<ContainerResponse>
}