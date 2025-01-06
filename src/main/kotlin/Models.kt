package org.example

import com.google.gson.annotations.SerializedName


data class Launch(
    @SerializedName("name") val missionName: String,
    @SerializedName("date_utc") val dateUtc: String,
    @SerializedName("rocket") val rocket: String,
    @SerializedName("success") val success: String,
    @SerializedName("launchpad") val launchSiteName: String,
    @SerializedName("id") val id: String
) {
    fun isSuccessLaunch(launch: Launch): Boolean {
        return launch.success == "true"
    }
}

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
    @SerializedName("mass_kg") val massKg: String,
    @SerializedName("orbit") val orbit: String,
    @SerializedName("launch") val launch: String
)

data class Rocket(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("stages") val stages: Int,
    @SerializedName("boosters") val boosters: Int,
    @SerializedName("mass") val mass: Mass,
    @SerializedName("payload_weights") val payloadWeights: List<PayloadWeight>
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
) {
    private val successRate: Int
        get() = if (totalLaunches != 0) {
            ((successfulLaunches.toDouble().div(totalLaunches)) * 100).toInt()
        } else {
            0
        }

    override fun toString(): String {
        return "Rocket Statistics for ${rocket.name}\n" +
                "Stages: ${rocket.stages}\n" +
                "Boosters: ${rocket.boosters}\n" +
                "Mass: ${rocket.mass.kg} kg\n" +
                "Payload to LEO: ${rocket.payloadWeights[0].kg} kg\n" +
                "Total Launches: ${totalLaunches}\n" +
                "Successful Launches: ${successfulLaunches}\n" +
                "Failed Launches: ${failedLaunches}\n" +
                "Success Rate: $successRate %"
    }
}