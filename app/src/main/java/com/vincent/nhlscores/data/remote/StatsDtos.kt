package com.vincent.nhlscores.data.remote

data class StatsScheduleDto(
    val games: List<StatsGame> = emptyList()
)

data class StatsGame(
    val id: Long? = null,
    val gamePk: Long? = null,
    val gameState: String? = null,
    val homeTeam: StatsTeamRef? = null,
    val awayTeam: StatsTeamRef? = null,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val period: Int? = null,
    val timeRemaining: String? = null
)

data class StatsTeamRef(
    val name: String? = null,
    val abbrev: String? = null,
    val score: Int? = null
)
