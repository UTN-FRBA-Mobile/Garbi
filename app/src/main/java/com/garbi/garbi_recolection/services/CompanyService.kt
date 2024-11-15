package com.garbi.garbi_recolection.services

import com.garbi.garbi_recolection.models.Report
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path


data class Company(
    val id: String,
    val threshold: Threshold
)

data class Threshold(
    val warning: Number,
    val full: Number
)

interface CompanyService {

    @GET("/integration/company/{id}")
    @Headers("accept: application/json")
    suspend fun getCompany(@Path("id") id: String): Response<Company>

}