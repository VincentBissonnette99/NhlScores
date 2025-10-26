package com.vincent.nhlscores.data.remote

data class PlayByPlayDto(
    val plays: List<PbpPlay> = emptyList()
)

data class PbpPlay(
    val type: String? = null,
    val periodDescriptor: WebPeriod? = null,
    val timeInPeriod: String? = null,
    val teamAbbrev: String? = null,
    val scoringPlay: PbpScoring? = null
)

data class PbpScoring(
    val scorer: PbpPlayer? = null,
    val assists: List<PbpPlayer>? = null
)

data class PbpPlayer(
    val playerId: Long? = null,
    val name: WebName? = null,
    val seasonTotal: Int? = null,
)

data class BoxscoreDto(
    val homeTeam: BoxTeam? = null,
    val awayTeam: BoxTeam? = null
)

data class BoxTeam(
    val abbrev: String? = null,
    val sog: Int? = null
)