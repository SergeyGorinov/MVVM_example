package com.example.mvvmexample.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingData(
    val items: List<GeocodingAddress>
)

@Serializable
data class GeocodingAddress(
    val address: GeoAddress,
    val position: Point
) {

    fun toAddress(): Address {
        return Address(
            lat = position.lat,
            lon = position.lng,
            city = address.city,
            building = address.houseNumber,
            street = address.street
        )
    }
}

@Serializable
data class GeoAddress(
    val label: String? = null,
    val street: String? = null,
    val houseNumber: String? = null,
    val city: String? = null
)

@Serializable
data class Point(
    val lat: Float,
    val lng: Float
)