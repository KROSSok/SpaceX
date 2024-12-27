package org.example

import retrofit2.Call
import retrofit2.http.GET

import retrofit2.http.Query


interface SpaceXApiService {

    @GET("launches")
    fun getLaunchesByYear(@Query("date_utc") year: String): Call<List<Launch>>

    @GET("payloads")
    fun getPayloadsById(@Query("id") launchId: String): Call<List<Payload>>

    @GET("rockets")
    fun getRocketStatistics(): Call<List<Rocket>>
}

/*
SpaceX Launch Tracker with Launch Success Rate Analysis
Description

Add new functionality to the already existing SpaceX app

Subtask 1: Add Payload Analysis

Description:
Introduce functionality to analyze payloads for each launch, including payload type, mass, and orbit.

Requirements:
Payload Data:
Fetch payload data from the SpaceX API (https://api.spacexdata.com/v4/payloads).
Map JSON fields such as name, type, mass_kg, and orbit to a Payload data class.

User Interaction:
Add a menu option to display payload details for a specific launch:

Enter your choice: 4
Enter the mission name: Starlink-15

Payload Details for Mission: Starlink-15
Payload Name: Starlink-15
Type: Satellite
Mass: 260 kg
Orbit: Low Earth Orbit

Caching:
Cache payload data by launch_id in a Map to avoid repeated API calls.

Error Handling:
Handle cases where payload data is unavailable or incomplete.

Subtask 2: Add Rocket Statistics
Description:
Allow users to view detailed statistics about SpaceX rockets, including total launches, success rates, and payload capacity.

Requirements:
Rocket Data:
Fetch rocket data from the SpaceX API (https://api.spacexdata.com/v4/rockets)
Map JSON fields such as name, stages, boosters, mass_kg, and payload_weights to a Rocket data class.

User Interaction:
Add a menu option to display rocket-specific statistics:

Enter your choice: 5
Enter the rocket name: Falcon 9

Rocket Statistics for Falcon 9
Stages: 2
Boosters: 0
Mass: 549,054 kg
Payload to LEO: 22,800 kg
Total Launches: 128
Successful Launches: 124
Failed Launches: 4
Success Rate: 96.9%

Calculation:

Use cached launch data to calculate total launches and success rates for each rocket.

Caching:
Cache rocket statistics to minimize API calls.

Error Handling:
Handle cases where rocket data is unavailable or invalid.


Updated Menu Example
With these functionalities, the updated menu could look like this:

Welcome to the SpaceX Launch Tracker!
1. Search Launches by Year
2. View Favorites
3. Remove from Favorites
4. View Payload Details
5. View Rocket Statistics
6. Exit
 */