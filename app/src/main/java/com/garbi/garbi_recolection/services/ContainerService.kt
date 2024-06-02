package com.garbi.garbi_recolection.services

import ContainerResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface ContainerService {
    @GET("/api/container")
    @Headers("accept: application/json", "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InN0cmluZ0BhZG1pbi5jb20iLCJpYXQiOjE3MTcwMjc2MDYsImV4cCI6MTcxNzExNDAwNn0.J8oICcLxWe6ggvyyLTWNi4xOOE4zQ3eQ3SQklHUMmb0")
    suspend fun getContainers(): ContainerResponse
}