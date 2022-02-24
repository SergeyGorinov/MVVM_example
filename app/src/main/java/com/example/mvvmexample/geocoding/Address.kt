package com.example.mvvmexample.geocoding

data class Address(
    val lat: Float,
    val lon: Float,
    val city: String? = null,
    val building: String? = null,
    val street: String? = null,
) {

    fun concatAddress(): String {
        return listOf(city, street, building).filter { !it.isNullOrBlank() }.joinToString(", ")
    }
}