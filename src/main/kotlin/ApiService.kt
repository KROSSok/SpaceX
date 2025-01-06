package org.example

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService (){

    private val apiService: SpaceXApiService by lazy {
        Retrofit.Builder().baseUrl("https://api.spacexdata.com/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SpaceXApiService::class.java)
    }

    fun getLaunchesByYear(year: String): Call<List<Launch>>{
        return apiService.getLaunchesByYear(year)
    }

    fun getRocketStatistics(): Call<List<Rocket>>{
        return apiService.getRocketStatistics()
    }
    fun getPayloadsById(launchId: String): Call<List<Payload>>{
        return apiService.getPayloadsById(launchId)
    }
}