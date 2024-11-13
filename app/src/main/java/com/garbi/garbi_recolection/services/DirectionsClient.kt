package com.garbi.garbi_recolection.services
import Container
import android.location.Location
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String?,
        @Query("key") apiKey: String,
        @Query("language") language: String = "es"
    ): DirectionsResponse
}

data class DirectionsResponse(
    val routes: List<Route>
)

data class Leg(
    val steps: List<Step>
)

data class Step (
    val start_location: LocationStep,
    val end_location: LocationStep,
    val html_instructions: String
)

data class LocationStep(
    val lat: Double,
    val lng: Double
)
data class Route(
    val polylines: List<OverviewPolyline>,
    val legs: List<Leg>,
    val containers: List<Container>
)

data class OverviewPolyline(
    val points: String
)

object DirectionsClient {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

    val directionsService: DirectionsService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsService::class.java)
    }
}
