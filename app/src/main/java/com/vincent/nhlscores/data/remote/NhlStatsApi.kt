package com.vincent.nhlscores.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NhlStatsApi {
    // Fenêtre de dates, souvent en, startDate, endDate
    // Exemple typique
    @GET("stats/rest/en/schedule")
    suspend fun scheduleRange(
        @Query("startDate") startDate: String, // YYYY-MM-DD
        @Query("endDate") endDate: String     // YYYY-MM-DD
    ): StatsScheduleDto
}