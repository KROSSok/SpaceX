package org.example

import java.util.*

class SpaceXApp {
    fun start() {

        //adding comment

        val launchService = LaunchService()
        val scanner = Scanner(System.`in`)

        while (true) {
            println("Welcome to the SpaceX Launch Tracker!")
            println("1. Search Launches by Year")
            println("2. View Favorites")
            println("3. Remove from Favorites")
            println("4. View Payload Details")
            println("5. View Rocket Statistics")
            println("6. Exit")

            println("Enter your choice:")
            when (scanner.nextLine().trim()) {
                "1" -> {
                    println("Enter year to search launches:")
                    val year = SearchCriteria(scanner.nextLine().toInt())
                    val launches = launchService.getLaunchesByYear(year.year)
                    try {
                        if (launches.isEmpty()) {
                            println("No launches found for this year.")
                        } else {
                            launchService.printLaunch(launches)
                            println("Enter the number to add to favorites, or 0 to skip:")
                            val choice = scanner.nextLine().toInt()
                            if (choice in 1..launches.size) {
                                launchService.addToFavorites(launches[choice - 1])
                            }
                        }
                    } catch (e: Exception) {
                        println("Error fetching launches: ${e.message}")
                    }

                }

                "2" -> {
                    println("Favorites List:")
                    launchService.viewFavorites()
                }

                "3" -> {
                    println("Enter launch ID to remove from favorites:")
                    val launchId = scanner.nextLine()
                    launchService.removeFromFavorites(launchId)
                }

                "4" -> {
                    println("Enter the mission name:")
                    val missionName = scanner.nextLine()

                    try {
                        val payload = launchService.getMissionIdByName(missionName)
                            ?.let { launchService.getPayloadById(it) }
                        if (payload != null) {
                            launchService.printPayload(payload, missionName)
                        }
                    } catch (e: Exception) {
                        println("Error fetching launches: ${e.message}")
                    }
                }

                "5" -> {
                    println("Enter the rocket name:")
                    val rocketName = scanner.nextLine()
                    launchService.getRockets()
                    try {
                        launchService.getRocketStats(rocketName).let { launchService.printRocketStatistic(it) }
                    } catch (e: Exception) {
                        println("${e.message}")
                    }

                }

                "6" -> {
                    println("Exiting the application.")
                    return
                }

                else -> println("Invalid choice, please try again.")
            }
        }

    }
}


fun main() {

    val app = SpaceXApp()
    app.start()
    //other comment

}


/*Description

Develop a Kotlin console application that allows users to track and save information about SpaceX launches.
The application should integrate with the SpaceX API to retrieve data about past and upcoming launches,
allowing users to filter launches by year and save them to a favorites list for easy access

Requirements:

    Core Functionalities:

        Launch Search:

            Search by Year: Allow users to filter launches by year and view relevant details (such as mission name, date, and rocket type).
            Display Launch Details: Display launch details including mission name, date, rocket type, and launch site.

        Favorites Management:

            Add to Favorites: Allow users to save specific launches to a favorites list.
            View Favorites: Display all saved launches, sorted by date added.
            Remove from Favorites: Enable users to remove launches from their favorites list.

    OOP Requirements:

        Define data classes for Launch, Favorites, and SearchCriteria.
        Create a service class to manage fetching launch data and handling favorites.
        Use encapsulation to hide complex data or business logic where appropriate.

    Collections:

        Use lists to manage search results and favorites.
        Use maps to cache search results by year to avoid repeated API calls.

    API Integration:

        Use Retrofit to make HTTP requests to the SpaceX API to fetch launch data.
        Parse JSON responses with Gson to extract key fields and populate data classes.
        Display relevant fields such as mission_name, date_utc, rocket_name, and launch_site_name.

    Date Management with JodaTime:

        Use JodaTime to parse and format launch dates.
        Add a timestamp when a launch is saved to the favorites list.

    Error Handling and Validation:

        Handle invalid user inputs and API errors gracefully.
        Prevent duplicate launches from being added to favorites.
        Validate user inputs for search criteria and display errors if they are invalid.

Acceptance Criteria:

    Users can search for SpaceX launches by year, and the application displays launch details.
    Users can add launches to a favorites list, with each favorite timestamped.
    The application caches recent search results by year to reduce API calls.
    The system provides error messages for invalid search criteria, unavailable launches, or duplicate favorites.

Implementation Steps:

    Set Up Project:

        Initialize a Kotlin project with Gradle in IntelliJ IDEA.
        Add dependencies for Retrofit, Gson, and JodaTime in the build.gradle.kts file:
        retrofit:2.9.0
        retrofit2:converter-gson:2.9.0
        gson:2.8.9
        joda-time:2.10.10

    Define Data Classes:

        Launch: Represents details about each launch, including fields for id, missionName, dateUtc, rocketName, and launchSiteName.
        SearchCriteria: Stores search parameters, including the year.
        Favorite: Stores launch information along with a dateAdded timestamp.

    Implement Core Functionality:

        Launch Search:

            Fetches SpaceX launches for a specified year.

        Favorites Management:

            Adds a specific launch to the favorites list.
            Lists all favorite launches, showing mission name and date.
            Removes a launch from the favorites list.

    Implement API Service:

        Set up Retrofit service interface for SpaceX API,
        defining endpoints for searching launches by year(https://api.spacexdata.com/v4/launches) use query parameter date_utc  to filter by the year.
        Map JSON fields using Gson to extract launch details for each mission.

    Implement User Interaction:

        Prompt the user to enter search criteria, display results, and allow users to save or remove launches from favorites.

Basic code execution
Welcome to the SpaceX Launch Tracker!
Search Launches by Year
View Favorites
Remove from Favorites
Exit

Enter your choice: 1
Enter year to search launches: 2021
Found 3 launches in 2021:
Mission: Starlink-15, Date: 2021-03-04, Rocket: Falcon 9, Site: Cape Canaveral
Mission: Crew-2, Date: 2021-04-22, Rocket: Falcon 9, Site: Kennedy Space Center
Mission: Transporter-2, Date: 2021-06-30, Rocket: Falcon 9, Site: Cape Canaveral

Enter the number to add to favorites, or 0 to skip: 2
Mission Crew-2 added to favorites!

Enter your choice: 2
Favorites List:
Mission: Crew-2 - Added on 2023-10-08
Enter your choice: 4
Exiting the application.*/