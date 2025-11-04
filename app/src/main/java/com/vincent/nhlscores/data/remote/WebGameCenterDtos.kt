package com.vincent.nhlscores.data.remote

data class PlayByPlayDto(
    val plays: List<PbpPlay>? = null,
    val actions: List<PbpPlay>? = null,
    val rosterSpots: List<RosterSpot>? = null,
    val homeTeamId: Long? = null,
    val awayTeamId: Long? = null,
    val homeAbbrev: String? = null,
    val awayAbbrev: String? = null
) {
    fun allPlays(): List<PbpPlay> = plays ?: actions ?: emptyList()
}

data class PbpPlay(
    val type: String? = null,
    val eventType: String? = null,
    val typeDescKey: String? = null,

    val periodDescriptor: WebPeriod? = null,
    val period: Int? = null,
    val timeInPeriod: String? = null,
    val time: String? = null,

    val teamAbbrev: String? = null,
    val teamTricode: String? = null,

    val details: PbpDetails? = null
)

data class PbpDetails(
    val scoringPlayerId: Long? = null,
    val assist1PlayerId: Long? = null,
    val assist2PlayerId: Long? = null,
    val scoringPlayerTotal: Int? = null,
    val assist1PlayerTotal: Int? = null,
    val assist2PlayerTotal: Int? = null,
    val eventOwnerTeamId: Long? = null
)

data class RosterSpot(
    val playerId: Long? = null,
    val firstName: WebName? = null,
    val lastName: WebName? = null
)