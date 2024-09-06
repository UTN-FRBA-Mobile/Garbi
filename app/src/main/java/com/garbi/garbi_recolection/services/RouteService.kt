package com.garbi.garbi_recolection.services

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.Response
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

interface RouteService {


    @GET("/integration/route/{id}")
    @Headers("accept: application/json")
    suspend fun getRoute(@Path("id") id: String): Response<RouteResponse>
}