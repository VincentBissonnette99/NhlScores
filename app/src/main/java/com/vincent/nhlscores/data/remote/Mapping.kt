package com.vincent.nhlscores.data.remote

import com.vincent.nhlscores.model.Game
import com.vincent.nhlscores.model.GameStatus

fun ScheduleDto.toGames(): List<Game> {
    val list = dates.firstOrNull()?.games.orEmpty()
    return list.map { g ->
        val ls = g.linescore
        Game(
            id = g.gamePk,
            home = g.teams.home.team.name,
            away = g.teams.away.team.name,
            homeScore = g.teams.home.score ?: 0,
            awayScore = g.teams.away.score ?: 0,
            status = when (g.status.detailedState) {
                "Scheduled", "Pre-Game" -> GameStatus.PRE
                "In Progress", "In Progress - Critical" -> GameStatus.LIVE
                "End of Period" -> GameStatus.INTERMISSION
                "Final", "Game Over" -> GameStatus.FINAL
                else -> GameStatus.PRE
            },
            period = ls?.currentPeriod,
            timeRemaining = ls?.currentPeriodTimeRemaining
        )
    }
}
