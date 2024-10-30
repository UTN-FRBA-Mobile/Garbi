package com.garbi.garbi_recolection.services

import com.garbi.garbi_recolection.models.Report
import com.garbi.garbi_recolection.models.ReportResponse
import com.garbi.garbi_recolection.models.Status
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Headers
import retrofit2.http.Path
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query


data class CreateReportResponse(
    val message: String
)

data class CreateReportRequest(
    val userId: String,
    val containerId: String,
    val title: String,
    val description: String?,
    val address: String?,
    val phone: String?,
    val email: String,
    var type: String,
    var image: String? = null
){
    fun requiredFieldsCompleted(): Boolean {
        return title.isNotEmpty() && type.isNotEmpty()
    }
}

interface ReportService {
    @GET("/integration/report/all")
    @Headers("accept: application/json")
    suspend fun getReports(@Query("userId") userId: String): Response<ReportResponse>

    @GET("/integration/report/{id}")
    @Headers("accept: application/json")
    suspend fun getReport(@Path("id") id: String): Response<Report>

    @POST("/integration/report")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun createReport(
        @Body createReport: RequestBody
    ): Response<CreateReportResponse>

    @DELETE("/integration/report/{id}")
    @Headers("accept: application/json")
    suspend fun deleteReport(@Path("id") id: String): Response<CreateReportResponse>

    @PUT("/integration/report/{id}")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun editReport(
        @Path("id") id: String,
        @Body createReport: RequestBody
    ): Response<CreateReportResponse>
}