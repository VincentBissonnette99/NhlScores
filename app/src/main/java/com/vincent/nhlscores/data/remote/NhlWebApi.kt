package com.vincent.nhlscores.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface NhlWebApi {

    @GET("v1/score/now")
    suspend fun scoreNow(): WebScoreNowDto

    @GET("v1/score/{yyyy-MM-dd}")
    suspend fun scoreByDate(@Path("yyyy-MM-dd") date: String): WebScoreNowDto

    @GET("v1/gamecenter/{gameId}/play-by-play")
    suspend fun playByPlay(@Path("gameId") gameId: Long): PlayByPlayDto

    @GET("v1/gamecenter/{gameId}/playbyplay")
    suspend fun playByPlayAlt(@Path("gameId") gameId: Long): PlayByPlayDto

    @GET("v1/gamecenter/{gameId}/boxscore")
    suspend fun boxscore(@Path("gameId") gameId: Long): BoxscoreDto

    @GET("v1/gamecenter/{gameId}/landing")
    suspend fun gameLanding(@Path("gameId") gameId: Long): WebGame
}