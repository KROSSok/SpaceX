package org.example

import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class LaunchService {

    private val favorites = mutableListOf<Favorite>()
    private val searchCache = mutableMapOf<Int, List<Launch>>()
    private val payloads = mutableMapOf<String, List<Payload>>()
    private var rockets = mutableListOf<Rocket>()

    private val apiService: SpaceXApiService by lazy {
        Retrofit.Builder().baseUrl("https://api.spacexdata.com/v4/").addConverterFactory(GsonConverterFactory.create())
            .build().create(SpaceXApiService::class.java)
    }
    // Fetch launches by year

    fun getLaunchesByYear(year: Int): List<Launch> {
        val searchYear = SearchCriteria(year)
        if (searchCache.containsKey(year)) {
            return searchCache[year]!!
        }

        val call = apiService.getLaunchesByYear(searchYear.year.toString())
        val response = call.execute()
        return if (response.isSuccessful) {
            val launches = response.body() ?: emptyList()
            searchCache[year] = launches
            dateFilter(searchYear)
        } else {
            throw Exception("Failed to fetch data from SpaceX API")
        }
    }

    // Add launch to favorites
    fun addToFavorites(launch: Launch) {
        if (favorites.any { it.launch.id == launch.id }) {
            println("Launch already in favorites.")
        } else {
            val timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss")
            favorites.add(Favorite(launch, timestamp))
            println("${launch.mission_name} added to favorites!")
        }
    }

    // View all favorites
    fun viewFavorites() {
        if (favorites.isEmpty()) {
            println("No favorites found.")
        } else {
            favorites.sortedBy { it.dateAdded }
                .forEach { println("Mission: ${it.launch.mission_name} - Added on ${it.dateAdded}, id: ${it.launch.id}") }
        }
    }

    // Remove a launch from favorites
    fun removeFromFavorites(launchId: String) {
        val index = favorites.indexOfFirst { it.launch.id == launchId }
        if (index != -1) {
            val removed = favorites.removeAt(index)
            println("${removed.launch.mission_name} removed from favorites!")
        } else {
            println("Launch not found in favorites.")
        }
    }

    fun getMissionIdByName(missionMame: String): String? {

        for (entry in searchCache.values) {

            val launch = entry.find { it.mission_name == missionMame }
            if (launch != null) {
                return launch.id
            }
        }
        // Return null if no matching launch was found
        println("No Payloads found for this mission")
        return null
    }

    fun getPayloadById(launchId: String): Payload? {
        if (payloads.containsKey(launchId)) {
            return payloadFilter(launchId)
        }

        val call = apiService.getPayloadsById(launchId)
        val response = call.execute()
        return if (response.isSuccessful) {
            val payload = response.body() ?: emptyList()
            payloads[launchId] = payload  // Cache the payload
            payloadFilter(launchId)
        } else {
            throw Exception("Failed to fetch data from SpaceX API")
        }
    }

    private fun payloadFilter(launchId: String): Payload? {

        for (payloadList in payloads.values) {
            // Find the Payload with the matching id in the list
            val payload = payloadList.find { it.launch == launchId }
            if (payload != null) {
                return payload // Return the Payload if found
            }
        }
        // Return null if no matching Payload was found
        return null
    }

    private fun dateFilter(searchCriteria: SearchCriteria): List<Launch> {
        return searchCache.getValue(searchCriteria.year).filter {
            ZonedDateTime.parse(it.date_utc, DateTimeFormatter.ISO_DATE_TIME).year.toString() == searchCriteria.year.toString()
        }
    }

    fun getRockets() {
        val call = apiService.getRocketStatistics()
        val response = call.execute()
        return if (response.isSuccessful) {
            val rocket = response.body() ?: emptyList()
            rockets = rocket.toMutableList()
        } else {
            throw Exception("Failed to fetch data from SpaceX API")
        }
    }

    private fun getRocketByName(name: String): Rocket? {
        return rockets.find { it.name == name }
    }

    fun getRocketStats(rocketName: String): RocketStats {
        var successfulLaunches = 0
        var totalLaunches = 0
        var failedLaunches = 0
        val rocket = getRocketByName(rocketName) ?: throw RocketNotFoundException("Rocket with name '$rocketName' not found.")

        // Check if searchCache is empty or null
        if (searchCache.isEmpty()) {
            throw CacheDataNotFoundException("No Launches found for rocket '$rocketName', try to search for Launches first")
        }

        // Iterate over cached launches and calculate success rate
        searchCache.forEach { (_, launches) ->
            val rocketLaunches = launches.filter { it.rocket == rocket.id }
            successfulLaunches += rocketLaunches.count { it.success == "true" }
            failedLaunches += rocketLaunches.count { it.success == "false" }
            totalLaunches = (successfulLaunches + failedLaunches)
        }
        val successRate: Int = if (totalLaunches == 0) 0 else ((successfulLaunches.toDouble() / totalLaunches) * 100).toInt()
        return RocketStats(rocket, totalLaunches, successfulLaunches, failedLaunches, successRate)
    }


    fun printLaunch(launches: List<Launch>) {
        launches.forEachIndexed { index, launch ->
            println(
                "${index + 1}. Mission_name: ${launch.mission_name}, " +
                        "launch_date: ${launch.date_utc}, " +
                        "rocket: ${launch.rocket}, " +
                        "launch_site_name: ${launch.launch_site_name}, " +
                        "id: ${launch.id}"
            )
        }
    }

    fun printPayload(payload: Payload, missionName: String) {
        println(
            "Payload Details for Mission: ${missionName}\n" +
                    "Payload Name: ${payload.name} \n" +
                    "Type: ${payload.type} \n" +
                    "Mass: ${payload.mass_kg} kg \n" +
                    "Orbit: ${payload.orbit}"
        )
    }

    fun printRocketStatistic(rocketStats: RocketStats) {
        println(
            "Rocket Statistics for ${rocketStats.rocket.name}\n" +
                    "Stages: ${rocketStats.rocket.stages}\n" +
                    "Boosters: ${rocketStats.rocket.boosters}\n" +
                    "Mass: ${rocketStats.rocket.mass.kg} kg\n" +
                    "Payload to LEO: ${rocketStats.rocket.payload_weights[0].kg} kg\n" +
                    "Total Launches: ${rocketStats.totalLaunches}\n" +
                    "Successful Launches: ${rocketStats.successfulLaunches}\n" +
                    "Failed Launches: ${rocketStats.failedLaunches}\n" +
                    "Success Rate: ${rocketStats.successRate} %"
        )
    }
}

class RocketNotFoundException(message: String) : Exception(message)
class CacheDataNotFoundException(message: String) : Exception(message)