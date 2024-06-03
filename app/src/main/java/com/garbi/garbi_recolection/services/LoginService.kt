package com.garbi.garbi_recolection.services

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val success: Boolean
)

interface LoginService {
    @POST("/public-api/login")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}