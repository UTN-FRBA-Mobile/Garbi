package com.garbi.garbi_recolection.services

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class LoginRequest(
    val personalEmail: String,
    val password: String,
    val tokenFCM: String?
)

data class LoginResponse(
    val token: String,
    val termsAndConditions: Boolean
)

data class SessionRequest(
    val token: String
)

data class UserDetails(
    val id: String,
    val companyId: String,
    val name: String,
    val surname: String,
    val personalPhone: String,
    val personalEmail: String,
    val companyEmail: String,
    val role: String,
    val password: String
)


data class User(
    val id: String,
    val imageUrl: String,
)

data class ChangePasswordRequest(
    val password: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val message: String
)

interface LoginService {
    @POST("/integration/user/authenticate")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>


    @POST("/integration/user/session")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun session(@Body sessionRequest: SessionRequest): Response<UserDetails>


    @GET("/integration/user/{id}")
    @Headers("accept: application/json")
    suspend fun getUser(@Path("id") id: String): Response<User>


    @POST("/integration/user/change_password")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Response<ChangePasswordResponse>

}