package com.vincent.nhlscores.data.remote

import com.vincent.nhlscores.model.Game
import com.vincent.nhlscores.model.GameStatus

private fun statusFrom(
    gameState: String?,
    inIntermission: Boolean?
): GameStatus {
    val s = gameState?.trim()?.uppercase()

    return when {
        s in setOf("FINAL", "GAME OVER", "OFF", "COMPLETED") -> GameStatus.FINAL

        s in setOf("LIVE", "IN PROGRESS", "IN_PROGRESS", "CRIT") ->
            if (inIntermission == true) GameStatus.INTERMISSION else GameStatus.LIVE

        s in setOf("PRE", "SCHEDULED", "PRE-GAME", "PREGAME", "FUT") -> GameStatus.PRE

        else -> GameStatus.PRE
    }
}

private fun teamLabel(t: WebTeamRef?, fallback: String): String {
    if (t == null) return fallback
    return t.abbrev
        ?: t.abbreviation
        ?: t.name?.default
        ?: fallback
}

fun WebScoreNowDto.toGames(): List<Game> = games.map { g ->
    val homeRef = g.homeTeam ?: g.home
    val awayRef = g.awayTeam ?: g.away
    Game(
        id = g.id ?: g.gamePk ?: -1L,
        home = teamLabel(homeRef, "Home"),
        away = teamLabel(awayRef, "Away"),
        homeScore = homeRef?.score ?: 0,
        awayScore = awayRef?.score ?: 0,
        status = statusFrom(g.gameState, g.clock?.inIntermission),
        period = g.periodDescriptor?.number ?: g.period,
        timeRemaining = g.periodDescriptor?.periodTimeRemaining ?: g.clock?.timeRemaining,
        startTimeUtc = g.startTimeUTC   // nouveau
    )
}

fun WebScheduleDto.toGames(): List<Game> = WebScoreNowDto(games).toGames()
