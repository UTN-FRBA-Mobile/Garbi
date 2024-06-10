package com.garbi.garbi_recolection.services

import com.garbi.garbi_recolection.models.Report
import com.garbi.garbi_recolection.models.ReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Headers
import retrofit2.http.Path
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part


data class CreateReportResponse(
    val success: Boolean,
    val message: String,
    val code: Number,
    val body: String,
    val errorBody: String
)

interface ReportService {
    @GET("/api/report/{userId}")
    @Headers("accept: application/json")
    suspend fun getReports(@Path("userId") userId: String): ReportResponse

    @GET("/api/report/{id}")
    @Headers("accept: application/json")
    suspend fun getReport(@Path("id") id: String): Report

    @Multipart
    @POST("/api/report")
    suspend fun createReport(
        @Part("report") report: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<CreateReportResponse>

}