package com.vincent.nhlscores.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface NhlWebApi {
    @GET("v1/score/now")
    suspend fun scoreNow(): WebScoreNowDto

    @GET("v1/score/{date}")
    suspend fun scheduleByDate(@Path("date") date: String): WebScheduleDto

    @GET("v1/gamecenter/{gameId}/play-by-play")
    suspend fun playByPlay(@Path("gameId") gameId: Long): PlayByPlayDto

    @GET("v1/gamecenter/{gameId}/boxscore")
    suspend fun boxscore(@Path("gameId") gameId: Long): BoxscoreDto
}