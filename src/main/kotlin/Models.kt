package org.example

import com.google.gson.annotations.SerializedName


data class Launch(
    @SerializedName("name") val mission_name: String,
    @SerializedName("date_utc") val date_utc: String,
    @SerializedName("rocket") val rocket: String,
    @SerializedName("success") val success: String,
    @SerializedName("launchpad") val launch_site_name: String,
    @SerializedName("id") val id: String
)

data class SearchCriteria(
    val year: Int
)

data class Favorite(
    val launch: Launch,
    val dateAdded: String
)

data class Payload(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("mass_kg") val mass_kg: String,
    @SerializedName("orbit") val orbit: String,
    @SerializedName("launch") val launch: String
)

data class Rocket(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("stages") val stages: Int,
    @SerializedName("boosters") val boosters: Int,
    @SerializedName("mass") val mass: Mass,
    @SerializedName("payload_weights") val payload_weights: List<PayloadWeight>
)

data class Mass(@SerializedName("kg") val kg: Double)

data class PayloadWeight(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("kg") val kg: Int
)

data class RocketStats(
    val rocket: Rocket,
    val totalLaunches: Int,
    val successfulLaunches: Int,
    val failedLaunches: Int,
    val successRate: Int
)