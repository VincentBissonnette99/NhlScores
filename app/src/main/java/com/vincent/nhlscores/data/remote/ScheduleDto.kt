package com.vincent.nhlscores.data.remote

data class ScheduleDto(val dates: List<DateItem> = emptyList())
data class DateItem(val games: List<GameItem> = emptyList())

data class GameItem(
    val gamePk: Long,
    val status: Status,
    val teams: Teams,
    val linescore: LineScore? = null
)
data class Status(val detailedState: String)
data class Teams(val home: TeamSide, val away: TeamSide)
data class TeamSide(val team: TeamInfo, val score: Int? = null)
data class TeamInfo(val name: String)
data class LineScore(
    val currentPeriod: Int? = null,
    val currentPeriodTimeRemaining: String? = null
)

// Placeholder pour plus tard
class GameFeedDto
