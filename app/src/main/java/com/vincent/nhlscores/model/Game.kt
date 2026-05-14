package com.vincent.nhlscores.model

enum class GameStatus { PRE, LIVE, INTERMISSION, FINAL }

data class Game(
    val id: Long,
    val home: String,
    val away: String,
    val homeLogo: String?,
    val awayLogo: String?,
    val homeScore: Int,
    val awayScore: Int,
    val status: GameStatus,
    val period: Int?,
    val timeRemaining: String?,
    val startTimeUtc: String? = null
)

val Game.isFinal: Boolean
    get() = status == GameStatus.FINAL

val Game.isLive: Boolean
    get() = status == GameStatus.LIVE || status == GameStatus.INTERMISSION
