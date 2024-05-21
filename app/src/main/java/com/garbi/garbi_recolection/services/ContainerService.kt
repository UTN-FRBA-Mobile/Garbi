package com.garbi.garbi_recolection.services

import ContainerResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface ContainerService {
    @GET("/api/container")
    @Headers("accept: application/json", "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InN0cmluZ0BhZG1pbi5jb20iLCJpYXQiOjE3MTYyNDkxODksImV4cCI6MTcxNjMzNTU4OX0.BD0MCZfJm3R2Y7at4RKOF3kNecORYmTdT9c7Z6q05kg")
    suspend fun getContainers(): ContainerResponse
}