package com.garbi.garbi_recolection.services

import Container
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path


data class RouteResponse(
    val directions: Direction,
    val containers: List<Container>
){
    fun toRoute(): Route {
        return Route(
            polylines =  directions.polylines.map { OverviewPolyline(it) },
            legs = directions.legs,
            containers = containers
        )
    }
}
data class Direction(
    val polylines: List<String>,
    val legs: List<Leg>
)


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