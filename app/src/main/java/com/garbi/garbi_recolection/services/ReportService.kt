package com.garbi.garbi_recolection.services

import com.garbi.garbi_recolection.models.Report
import com.garbi.garbi_recolection.models.ReportResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Headers
import retrofit2.http.Body


data class CreateReportResponse(
    val token: String,
    val success: Boolean
)

interface ReportService {
    @GET("/api/report")
    @Headers("accept: application/json")
    suspend fun getReports(): ReportResponse

    @POST("/api/report")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun createReport(@Body createReport: Report): CreateReportResponse
}