package com.vincent.nhlscores.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface NhlWebApi {
    // Exemples vus dans les docs communautaires
    // 1) instantané des scores en cours
    @GET("v1/score/now")
    suspend fun scoreNow(): WebScoreNowDto

    // 2) calendrier du jour
    @GET("v1/schedule/{date}") // date au format YYYY-MM-DD
    suspend fun scheduleByDate(@Path("date") date: String): WebScheduleDto
}
