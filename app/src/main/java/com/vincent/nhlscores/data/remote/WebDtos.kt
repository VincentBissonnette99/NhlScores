package com.vincent.nhlscores.data.remote

data class WebScoreNowDto(
    val games: List<WebGame> = emptyList()
)

data class WebScheduleDto(
    val games: List<WebGame> = emptyList()
)

data class WebGame(
    val id: Long? = null,
    val gamePk: Long? = null,
    val gameDate: String? = null,
    val startTimeUTC: String? = null,
    val gameState: String? = null,
    val gameScheduleState: String? = null,
    val period: Int? = null,
    val periodDescriptor: WebPeriod? = null,
    val clock: WebClock? = null,
    val homeTeam: WebTeamRef? = null,
    val awayTeam: WebTeamRef? = null,
    val home: WebTeamRef? = null,
    val away: WebTeamRef? = null,
    val goals: List<WebGoal>? = null
)

data class WebTeamRef(
    val id: Int? = null,
    val abbrev: String? = null,
    val abbreviation: String? = null,
    val name: WebName? = null,
    val score: Int? = null,
    val sog: Int? = null,
    val logo: String? = null
)

data class WebName(
    val default: String? = null
)

data class WebPeriod(
    val number: Int? = null,
    val periodType: String? = null,
    val periodTimeRemaining: String? = null
)

data class WebClock(
    val timeRemaining: String? = null,
    val secondsRemaining: Int? = null,
    val running: Boolean? = null,
    val inIntermission: Boolean? = null
)

data class WebGoal(
    val period: Int? = null,
    val periodDescriptor: WebPeriod? = null,
    val timeInPeriod: String? = null,
    val teamAbbrev: String? = null,
    val playerId: Long? = null,
    val name: WebName? = null,
    val assists: List<WebAssist>? = null,
    val goalsToDate: Int? = null,
    val seasonTotal: Int? = null,
    val playerTotal: Int? = null,
    val scorerSeasonTotal: Int? = null
)

data class WebAssist(
    val playerId: Long? = null,
    val name: WebName? = null,
    val assistsToDate: Int? = null
)