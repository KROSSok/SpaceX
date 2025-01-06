package org.example

import org.joda.time.DateTime
import java.lang.Integer.sum
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class LaunchService {

    private val favorites = mutableListOf<Favorite>()
    private val searchCache = mutableMapOf<Int, List<Launch>>()
    private val payloads = mutableMapOf<String, List<Payload>>()
    private var rockets = mutableListOf<Rocket>()
    private val apiService = ApiService()


    // Fetch launches by year

    fun getLaunchesByYear(year: Int): List<Launch>? {
        val searchYear = SearchCriteria(year)
        if (searchCache.containsKey(year)) {
            return searchCache[year]!!
        }

        val call = apiService.getLaunchesByYear(searchYear.year.toString())
        val response = call.execute()
        return if (response.isSuccessful) {
            val launches = response.body()
            if (launches.isNullOrEmpty()) {
                println("No Launches found for this year")
                return null
            } else {
                searchCache[year] = launches
                dateFilter(searchYear)
            }
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
            println("${launch.missionName} added to favorites!")
        }
    }

    // View all favorites
    fun viewFavorites() {
        if (favorites.isEmpty()) {
            println("No favorites found.")
        } else {
            favorites.sortedBy { it.dateAdded }
                .forEach { println("Mission: ${it.launch.missionName} - Added on ${it.dateAdded}, id: ${it.launch.id}") }
        }
    }

    // Remove a launch from favorites
    fun removeFromFavorites(launchId: String) {
        val index = favorites.indexOfFirst { it.launch.id == launchId }
        if (index != -1) {
            val removed = favorites.removeAt(index)
            println("${removed.launch.missionName} removed from favorites!")
        } else {
            println("Launch not found in favorites.")
        }
    }

    fun getMissionIdByName(missionMame: String): String? {

        for (entry in searchCache.values) {

            val launch = entry.find { it.missionName.equals(missionMame, ignoreCase = true) }
            if (launch != null) {
                return launch.id
            }
        }
        // Return null if no matching launch was found
        if (searchCache.isEmpty()) {
            println("No Payloads found for this mission, try to search for launches first")
        } else println("Mission with $missionMame not found")
        return null
    }

    fun getPayloadById(launchId: String): Payload? {
        if (payloads.containsKey(launchId)) {
            return payloadFilter(launchId)
        }

        val call = apiService.getPayloadsById(launchId)
        val response = call.execute()
        return if (response.isSuccessful) {
            val payload = response.body()
            if (payload.isNullOrEmpty()) {
                println("No Payloads found for this mission")
                return null
            } else {
                payloads[launchId] = payload  // Cache the payload
                payloadFilter(launchId)
            }
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
            ZonedDateTime.parse(
                it.dateUtc,
                DateTimeFormatter.ISO_DATE_TIME
            ).year.toString() == searchCriteria.year.toString()
        }
    }

    private fun getRocketByName(name: String): Rocket? {
        return rockets.find { it.name == name }
    }

    private fun getRocketStats(rocketName: String?): RocketStats {
        val rocket = rocketName?.let { getRocketByName(it) } ?: throw RocketNotFoundException("Rocket with '$rocketName' not found")
        // Check if searchCache is empty or null
        if (searchCache.isEmpty()) {
            throw CacheDataNotFoundException("No Launches found for rocket '$rocketName', try to search for Launches first")
        }

        val rocketLaunches = searchCache.entries.firstOrNull()?.value?.filter { it.rocket == rocket.id }

        if (rocketLaunches.isNullOrEmpty()){
            throw CacheDataNotFoundException("No Launches found for rocket '$rocketName'")
        }
        val rocketStats = RocketStats(
            rocket = rocket,
            totalLaunches = sum (rocketLaunches.count { it.isSuccessLaunch(it) }, rocketLaunches.count { !it.isSuccessLaunch(it) }),
            successfulLaunches = rocketLaunches.count { it.isSuccessLaunch(it) },
            failedLaunches = rocketLaunches.count { !it.isSuccessLaunch(it) }
        )
        return rocketStats
    }


    fun printLaunch(launches: List<Launch>) {
        launches.forEachIndexed { index, launch ->
            println(
                "${index + 1}. Mission_name: ${launch.missionName}, " +
                        "launch_date: ${launch.dateUtc}, " +
                        "rocket: ${launch.rocket}, " +
                        "launch_site_name: ${launch.launchSiteName}, " +
                        "id: ${launch.id}"
            )
        }
    }

    fun printPayload(payload: Payload, missionName: String) {
        println(
            "Payload Details for Mission: ${missionName}\n" +
                    "Payload Name: ${payload.name} \n" +
                    "Type: ${payload.type} \n" +
                    "Mass: ${payload.massKg ?: 0} kg \n" +
                    "Orbit: ${payload.orbit}"
        )
    }

    fun findRocketByName(rocketName: String) {
        rocketName.let { name ->
            if (rockets.find { it.name == name } == null) {
                val call = apiService.getRocketStatistics()
                val response = call.execute()
                return if (response.isSuccessful) {
                    val responseData = response.body()?: emptyList()
                    rockets = responseData.toMutableList()
                    val rocket = responseData.find { it.name == name }
                    println(getRocketStats(rocket?.name).toString())
                } else {
                    throw Exception("Failed to fetch data from SpaceX API")
                }
            } else {
                println(getRocketStats(rocketName).toString())
            }
        }
    }
}

class RocketNotFoundException(message: String) : Exception(message)
class CacheDataNotFoundException(message: String) : Exception(message)