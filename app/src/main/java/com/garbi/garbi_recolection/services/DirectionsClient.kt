package com.garbi.garbi_recolection.services
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
        @Query("key") apiKey: String
    ): DirectionsResponse
}

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val overview_polyline: OverviewPolyline
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
