data class Address(
    val street: String,
    val number: String,
    val neighborhood: String
) {
    fun convertToString(): String {
        return street + " " + number
    }
}

data class Coordinates(
    val lat: Double,
    val lng: Double
)
data class Container(
    val areaId: String,
    val sensorId: String,
    val address: Address,
    val coordinates: Coordinates,
    val height: Int,
    val capacity: Int,
    val batery: Int
)

data class ContainerResponse(
    val documents: List<Container>,
    val total: Int
)