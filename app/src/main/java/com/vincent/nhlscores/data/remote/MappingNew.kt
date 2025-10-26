package com.vincent.nhlscores.data.remote

import com.vincent.nhlscores.model.*

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
    return t.abbrev ?: t.abbreviation ?: t.name?.default ?: fallback
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
        startTimeUtc = g.startTimeUTC
    )
}

fun WebScheduleDto.toGames(): List<Game> = WebScoreNowDto(games).toGames()

fun WebGame.toDetail(): GameDetail {
    val homeRef = homeTeam ?: home
    val awayRef = awayTeam ?: away

    val perScorerCount = mutableMapOf<String, Int>()

    val goalsList = goals.orEmpty().map { ev ->
        val scorerName = ev.name?.default?.trim().orEmpty()
        val nth = perScorerCount.getOrDefault(scorerName, 0) + 1
        perScorerCount[scorerName] = nth
        val seasonTotal =
            ev.goalsToDate
                ?: ev.scorerSeasonTotal
                ?: ev.seasonTotal
                ?: ev.playerTotal
        val assistsLabels = ev.assists.orEmpty().mapNotNull { a ->
            val n = a.name?.default?.trim()
            val tot = a.assistsToDate
            when {
                n.isNullOrBlank() -> null
                tot != null -> "$n ($tot)"
                else -> n
            }
        }

        GoalEvent(
            period = ev.periodDescriptor?.number ?: ev.period ?: 0,
            timeInPeriod = ev.timeInPeriod ?: "",
            team = ev.teamAbbrev ?: "",
            scorer = scorerName,
            assists = assistsLabels,
            scorerSeasonTotal = seasonTotal,
            nthOfGame = nth
        )
    }

    return GameDetail(
        id = id ?: gamePk ?: -1L,
        home = teamLabel(homeRef, "Home"),
        away = teamLabel(awayRef, "Away"),
        homeScore = homeRef?.score ?: 0,
        awayScore = awayRef?.score ?: 0,
        homeSog = homeRef?.sog ?: 0,
        awaySog = awayRef?.sog ?: 0,
        status = statusFrom(gameState, clock?.inIntermission),
        period = periodDescriptor?.number ?: period,
        timeRemaining = periodDescriptor?.periodTimeRemaining ?: clock?.timeRemaining,
        startTimeUtc = startTimeUTC,
        goals = goalsList
    )
}

fun mergeDetailWithPbp(
    base: GameDetail,
    pbp: PlayByPlayDto?
): GameDetail {
    if (pbp == null) return base

    val enriched = base.goals.map { g ->
        val match = pbp.plays.firstOrNull { p ->
            p.type?.equals("goal", ignoreCase = true) == true &&
                    (p.periodDescriptor?.number ?: -1) == g.period &&
                    (p.timeInPeriod ?: "").equals(g.timeInPeriod, ignoreCase = true) &&
                    (p.teamAbbrev ?: "").equals(g.team, ignoreCase = true) &&
                    p.scoringPlay?.scorer?.name?.default?.trim().orEmpty()
                        .equals(g.scorer, ignoreCase = true)
        }
        val seasonTotal = match?.scoringPlay?.scorer?.seasonTotal ?: g.scorerSeasonTotal
        g.copy(scorerSeasonTotal = seasonTotal)
    }

    return base.copy(goals = enriched)
}

fun mergeDetailWithBox(
    base: GameDetail,
    box: BoxscoreDto?
): GameDetail {
    if (box == null) return base
    return base.copy(
        homeSog = box.homeTeam?.sog ?: base.homeSog,
        awaySog = box.awayTeam?.sog ?: base.awaySog
    )
}
