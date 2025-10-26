package com.vincent.nhlscores.model

data class GoalEvent(
    val period: Int,
    val timeInPeriod: String,
    val team: String,
    val scorer: String,
    val assists: List<String>,
    val scorerSeasonTotal: Int?,
    val nthOfGame: Int
)

data class GameDetail(
    val id: Long,
    val home: String,
    val away: String,
    val homeScore: Int,
    val awayScore: Int,
    val homeSog: Int,
    val awaySog: Int,
    val status: GameStatus,
    val period: Int?,
    val timeRemaining: String?,
    val startTimeUtc: String?,
    val goals: List<GoalEvent>
)
