package com.vincent.nhlscores.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.nhlscores.data.remote.HttpClient
import com.vincent.nhlscores.data.remote.toGames
import com.vincent.nhlscores.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TodayViewModel : ViewModel() {

    private val tz = ZoneId.of("America/Toronto")
    private val apiFmt = DateTimeFormatter.ISO_LOCAL_DATE

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _currentDate = MutableStateFlow(todayToronto())
    val currentDate: StateFlow<LocalDate> = _currentDate

    init {
        loadFor(_currentDate.value)
    }

    fun refresh() {
        if (_loading.value) return
        loadFor(_currentDate.value)
    }

    fun goToPreviousDay() {
        val min = todayToronto().minusDays(7)
        val next = _currentDate.value.minusDays(1)
        if (next.isBefore(min)) return
        _currentDate.value = next
        loadFor(next)
    }

    fun goToNextDay() {
        val max = todayToronto().plusDays(7)
        val next = _currentDate.value.plusDays(1)
        if (next.isAfter(max)) return
        _currentDate.value = next
        loadFor(next)
    }

    private fun todayToronto(): LocalDate = LocalDate.now(tz)

    private fun loadFor(date: LocalDate) {
        viewModelScope.launch {
            if (_loading.value) return@launch
            _loading.value = true
            try {
                val dateStr = date.format(apiFmt)

                // 1, toujours charger exactement la date demandée
                val scheduleGames = runCatching {
                    HttpClient.webApi.scheduleByDate(dateStr).toGames()
                }.onFailure { e ->
                    Log.e("NHL", "scheduleByDate $dateStr failed", e)
                }.getOrDefault(emptyList())

                // 2, si c’est aujourd’hui, enrichir avec le snapshot live
                val finalGames = if (date.isEqual(todayToronto())) {
                    val liveGames = runCatching {
                        HttpClient.webApi.scoreNow().toGames()
                    }.onFailure { e ->
                        Log.w("NHL", "scoreNow failed, ${e.message}")
                    }.getOrDefault(emptyList())

                    if (liveGames.isNotEmpty()) {
                        mergeScheduleWithLive(scheduleGames, liveGames)
                    } else {
                        scheduleGames
                    }
                } else {
                    scheduleGames
                }

                _games.value = finalGames
            } finally {
                _loading.value = false
            }
        }
    }

    private fun mergeScheduleWithLive(schedule: List<Game>, live: List<Game>): List<Game> {
        if (schedule.isEmpty()) return live
        if (live.isEmpty()) return schedule

        val liveById = live.associateBy { it.id }
        return schedule.map { s ->
            val l = liveById[s.id]
            if (l == null) s else s.copy(
                homeScore = l.homeScore,
                awayScore = l.awayScore,
                status = l.status,
                period = l.period,
                timeRemaining = l.timeRemaining,
                startTimeUtc = l.startTimeUtc ?: s.startTimeUtc
            )
        }
    }
}