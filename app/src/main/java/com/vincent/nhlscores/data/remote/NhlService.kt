package com.vincent.nhlscores.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NhlService {
    // Exemple: https://statsapi.web.nhl.com/api/v1/schedule?date=2025-10-25&expand=schedule.teams,schedule.linescore
    @GET("api/v1/schedule")
    suspend fun schedule(
        @Query("date") date: String,
        @Query("expand") expand: String = "schedule.teams,schedule.linescore"
    ): ScheduleDto

    @GET("api/v1/game/{gamePk}/feed/live")
    suspend fun gameFeed(@Path("gamePk") gamePk: Long): GameFeedDto
}
