package com.garbi.garbi_recolection.services

import ContainerResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface ContainerService {
    @GET("/api/container")
    @Headers("accept: application/json")
    suspend fun getContainers(): ContainerResponse
}