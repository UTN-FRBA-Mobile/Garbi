import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ContainerClusterItem(
    private val container: Container
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(container.coordinates.lat, container.coordinates.lng)
    override fun getTitle(): String = container.address.convertToString() // Puedes personalizarlo según necesites
    override fun getSnippet(): String = "Capacidad: ${container.capacity}%" // Puedes personalizarlo según necesites
    override fun getZIndex(): Float = 1f

    fun getContainer(): Container = container
}

data class Address(
    val street: String,
    val number: String,
    val neighborhood: String
) {
    fun convertToString(): String {
        return "$street $number - $neighborhood"
    }
    fun convertToStringReport(): String {
        return "$street $number, $neighborhood"
    }
}

data class Coordinates(
    val lat: Double,
    val lng: Double
)
data class Container(
    val id: String,
    val areaId: String,
    val sensorId: String,
    val address: Address,
    val coordinates: Coordinates,
    val height: Int,
    val capacity: Int,
    val battery: Int
)

data class ContainerResponse(
    val result: List<Container>?,
    val total: Int?,
    val limit: Int?,
    val message: String?
)