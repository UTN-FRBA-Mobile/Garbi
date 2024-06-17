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
    val success: Boolean,
    val termsAndConditions: Boolean
)

data class SessionRequest(
    val token: String
)

data class SessionResponse(
    val success: Boolean,
    val user: UserDetails
)

data class UserDetails(
    val _id: String,
    val companyId: String,
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    val role: String
)

data class ChangePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val success: Boolean
)


interface LoginService {
    @POST("/public-api/login")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse


    @POST("/public-api/session")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun session(@Body sessionRequest: SessionRequest): SessionResponse


    @POST("/public-api/change_password")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): ChangePasswordResponse

}