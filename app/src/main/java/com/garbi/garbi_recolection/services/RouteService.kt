package com.garbi.garbi_recolection.services

import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path


data class RouteResponse(
    val directions: Direction
)
data class Direction(
    val overview_polyline: String,
    val legs: List<Leg>
){
    fun toRoute(): Route {
        return Route(
            overview_polyline = OverviewPolyline(overview_polyline),
            legs = legs
        )
    }
}


data class RouteStatusResponse(
    val message: String
)

interface RouteService {


    @GET("/integration/route/{id}")
    @Headers("accept: application/json")
    suspend fun getRoute(@Path("id") id: String): Response<RouteResponse>


    @PUT("/integration/route/start/{id}")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun startRoute(
        @Path("id") id: String
    ): Response<RouteStatusResponse>
    @PUT("/integration/route/finish/{id}")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun finishRoute(
        @Path("id") id: String
    ): Response<RouteStatusResponse>
}