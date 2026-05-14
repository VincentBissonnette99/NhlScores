package com.vincent.nhlscores.data.remote

import com.vincent.nhlscores.model.Game
import com.vincent.nhlscores.model.GameDetail
import com.vincent.nhlscores.model.GameStatus
import com.vincent.nhlscores.model.GoalEvent

private fun statusFrom(gameState: String?, inIntermission: Boolean?): GameStatus {
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

private fun gameIdOf(g: WebGame): Long = g.id ?: g.gamePk ?: -1L

fun WebScoreNowDto.toGames(): List<Game> = games.map { g ->
    val homeRef = g.homeTeam ?: g.home
    val awayRef = g.awayTeam ?: g.away
    Game(
        id = gameIdOf(g),
        home = teamLabel(homeRef, "Home"),
        away = teamLabel(awayRef, "Away"),
        homeLogo = homeRef?.logo,
        awayLogo = awayRef?.logo,
        homeScore = homeRef?.score ?: 0,
        awayScore = awayRef?.score ?: 0,
        status = statusFrom(g.gameState, g.clock?.inIntermission),
        period = g.periodDescriptor?.number ?: g.period,
        timeRemaining = g.periodDescriptor?.periodTimeRemaining ?: g.clock?.timeRemaining,
        startTimeUtc = g.startTimeUTC
    )
}

fun WebScheduleDto.toGames(): List<Game> = WebScoreNowDto(this.games).toGames()

fun WebGame.toDetail(): GameDetail {
    val homeRef = homeTeam ?: home
    val awayRef = awayTeam ?: away

    val perScorerCount = mutableMapOf<String, Int>()
    val goalsList = goals.orEmpty().map { ev ->
        val scorerName = ev.name?.default?.trim().orEmpty()
        val nth = perScorerCount.getOrDefault(scorerName, 0) + 1
        perScorerCount[scorerName] = nth
        val seasonTotal = ev.scorerSeasonTotal ?: ev.seasonTotal ?: ev.playerTotal
        GoalEvent(
            period = ev.periodDescriptor?.number ?: ev.period ?: 0,
            timeInPeriod = ev.timeInPeriod ?: "",
            team = ev.teamAbbrev ?: "",
            scorer = scorerName,
            assists = ev.assists.orEmpty().mapNotNull { it.name?.default },
            scorerSeasonTotal = seasonTotal,
            nthOfGame = nth,
            assistsSeasonTotals = ev.assists.orEmpty().mapNotNull { it.assistsToDate }
        )
    }

    return GameDetail(
        id = gameIdOf(this),
        home = teamLabel(homeRef, "Home"),
        away = teamLabel(awayRef, "Away"),
        homeLogo = homeRef?.logo,
        awayLogo = awayRef?.logo,
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

fun GameDetail.mergeDetailWithLinescore(lines: LinescoreDto?): GameDetail {
    if (lines == null) return this
    val newStatus = statusFrom(lines.gameState, lines.clock?.inIntermission)
    val newPeriod = lines.periodDescriptor?.number ?: lines.period
    val newTime = lines.periodDescriptor?.periodTimeRemaining ?: lines.clock?.timeRemaining

    val homeTeamName = lines.homeTeam?.abbrev ?: lines.homeTeam?.name?.default ?: this.home
    val awayTeamName = lines.awayTeam?.abbrev ?: lines.awayTeam?.name?.default ?: this.away
    val homeScore = lines.homeTeam?.score ?: this.homeScore
    val awayScore = lines.awayTeam?.score ?: this.awayScore

    return copy(
        status = newStatus,
        period = newPeriod,
        timeRemaining = newTime,
        home = homeTeamName,
        away = awayTeamName,
        homeScore = homeScore,
        awayScore = awayScore
    )
}

fun GameDetail.mergeDetailWithBox(box: BoxscoreDto?): GameDetail {
    if (box == null) return this
    val homeSog = box.homeTeam?.sog ?: this.homeSog
    val awaySog = box.awayTeam?.sog ?: this.awaySog
    return copy(homeSog = homeSog, awaySog = awaySog)
}

fun GameDetail.mergeDetailWithPbp(pbp: PlayByPlayDto?): GameDetail {
    if (pbp == null) return this

    // index roster for playerId -> "First Last"
    val nameById: Map<Long, String> = pbp.rosterSpots.orEmpty().associate { rs ->
        val id = rs.playerId ?: -1L
        val fn = rs.firstName?.default?.trim().orEmpty()
        val ln = rs.lastName?.default?.trim().orEmpty()
        id to listOf(fn, ln).filter { it.isNotEmpty() }.joinToString(" ")
    }

    val counter = mutableMapOf<String, Int>()
    val newGoals = pbp.allPlays().asSequence()
        .filter { p ->
            val k = p.typeDescKey?.lowercase() ?: p.eventType?.lowercase() ?: p.type?.lowercase()
            k == "goal" || k == "goals"
        }
        .map { p ->
            val period = p.periodDescriptor?.number ?: p.period ?: 0
            val time = p.timeInPeriod ?: p.time ?: ""
            val team = p.teamAbbrev ?: p.teamTricode ?: ""
            val d = p.details
            val scorerName = if (d?.scoringPlayerId != null) {
                nameById[d.scoringPlayerId] ?: ""
            } else ""
            val a1 = if (d?.assist1PlayerId != null) nameById[d.assist1PlayerId] else null
            val a2 = if (d?.assist2PlayerId != null) nameById[d.assist2PlayerId] else null
            val assists = listOfNotNull(a1, a2)
            val assistsSeasonTotals = listOfNotNull(d?.assist1PlayerTotal, d?.assist2PlayerTotal)

            val nth = if (scorerName.isNotEmpty()) {
                val n = counter.getOrDefault(scorerName, 0) + 1
                counter[scorerName] = n
                n
            } else 0

            val seasonTotal = d?.scoringPlayerTotal

            GoalEvent(
                period = period,
                timeInPeriod = time,
                team = team,
                scorer = scorerName,
                assists = assists,
                scorerSeasonTotal = seasonTotal,
                nthOfGame = nth,
                assistsSeasonTotals = assistsSeasonTotals
            )
        }
        .toList()

    val merged = if (this.goals.isNullOrEmpty()) newGoals else this.goals + newGoals
    return copy(goals = merged)
}
fun PlayByPlayDto.toGameDetail(gameId: Long): GameDetail {
    val homeAbbrev = this.homeAbbrev ?: "HOME"
    val awayAbbrev = this.awayAbbrev ?: "AWAY"

    val nameById: Map<Long, String> = this.rosterSpots.orEmpty().associate { rs ->
        val id = rs.playerId ?: -1L
        val fn = rs.firstName?.default?.trim().orEmpty()
        val ln = rs.lastName?.default?.trim().orEmpty()
        id to listOf(fn, ln).filter { it.isNotEmpty() }.joinToString(" ")
    }

    val counter = mutableMapOf<String, Int>()
    val goals = this.allPlays().asSequence()
        .filter { p ->
            val k = p.typeDescKey?.lowercase() ?: p.eventType?.lowercase() ?: p.type?.lowercase()
            k == "goal" || k == "goals"
        }
        .map { p ->
            val period = p.periodDescriptor?.number ?: p.period ?: 0
            val time = p.timeInPeriod ?: p.time ?: ""
            val team = p.teamAbbrev ?: p.teamTricode ?: ""
            val d = p.details
            val scorerName = if (d?.scoringPlayerId != null) {
                nameById[d.scoringPlayerId] ?: ""
            } else ""
            val a1 = if (d?.assist1PlayerId != null) nameById[d.assist1PlayerId] else null
            val a2 = if (d?.assist2PlayerId != null) nameById[d.assist2PlayerId] else null
            val assists = listOfNotNull(a1, a2)
            val assistsSeasonTotals = listOfNotNull(d?.assist1PlayerTotal, d?.assist2PlayerTotal)

            val nth = if (scorerName.isNotEmpty()) {
                val n = counter.getOrDefault(scorerName, 0) + 1
                counter[scorerName] = n
                n
            } else 0

            GoalEvent(
                period = period,
                timeInPeriod = time,
                team = team,
                scorer = scorerName,
                assists = assists,
                scorerSeasonTotal = d?.scoringPlayerTotal,
                nthOfGame = nth,
                assistsSeasonTotals = assistsSeasonTotals
            )
        }
        .toList()

    return GameDetail(
        id = gameId,
        home = homeAbbrev,
        away = awayAbbrev,
        homeLogo = null,
        awayLogo = null,
        homeScore = 0,
        awayScore = 0,
        homeSog = 0,
        awaySog = 0,
        status = GameStatus.LIVE,
        period = null,
        timeRemaining = null,
        startTimeUtc = null,
        goals = goals
    )
}