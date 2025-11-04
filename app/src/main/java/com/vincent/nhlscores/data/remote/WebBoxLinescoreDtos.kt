package com.vincent.nhlscores.data.remote

data class BoxscoreDto(
    val homeTeam: BoxTeam? = null,
    val awayTeam: BoxTeam? = null
)

data class BoxTeam(
    val sog: Int? = null
)

data class LinescoreDto(
    val gameState: String? = null,
    val period: Int? = null,
    val periodDescriptor: WebPeriod? = null,
    val clock: WebClock? = null,
    val homeTeam: LinescoreTeam? = null,
    val awayTeam: LinescoreTeam? = null
)

data class LinescoreTeam(
    val abbrev: String? = null,
    val name: WebName? = null,
    val score: Int? = null
)
