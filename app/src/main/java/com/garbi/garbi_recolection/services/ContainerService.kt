package com.garbi.garbi_recolection.services

import ContainerResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface ContainerService {
    @GET("/api/container")
    @Headers("accept: application/json", "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InN0cmluZ0BhZG1pbi5jb20iLCJpYXQiOjE3MTY4MjQ4MjIsImV4cCI6MTcxNjkxMTIyMn0.6nN3XR1srUC92ThQtBIkPd0sln8bXTuZ_55lMS_W39Y")
    suspend fun getContainers(): ContainerResponse
}