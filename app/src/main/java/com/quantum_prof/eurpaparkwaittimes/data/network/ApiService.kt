package com.quantum_prof.eurpaparkwaittimes.data.network


import com.quantum_prof.eurpaparkwaittimes.data.AttractionWaitTime
import com.quantum_prof.eurpaparkwaittimes.data.CrowdLevel
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("v1/waitingtimes")
    suspend fun getWaitTimes(
        @Header("park") parkId: String = "europapark",
        @Header("language") language: String = "de"
    ): List<AttractionWaitTime>

    @GET("v1/crowdlevel")
    suspend fun getCrowdLevel(
        @Header("park") parkId: String = "europapark"
    ): CrowdLevel
}