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

class TodayViewModel : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadToday()
    }

    fun refresh() {
        if (_loading.value) return
        loadToday()
    }

    private fun todayTorontoStr(): String =
        LocalDate.now(ZoneId.of("America/Toronto")).toString()

    private fun loadToday() {
        viewModelScope.launch {
            if (_loading.value) return@launch
            _loading.value = true
            try {
                val date = todayTorontoStr()
                var result: List<Game> = emptyList()

                // Essai 1, instantané du moment
                runCatching { HttpClient.webApi.scoreNow().toGames() }
                    .onSuccess { list ->
                        if (list.isNotEmpty()) {
                            result = list
                            Log.d("NHL", "scoreNow ok, ${list.size} matchs")
                        }
                    }
                    .onFailure { e ->
                        Log.w("NHL", "scoreNow failed, ${e.message}")
                    }

                // Essai 2, journée exacte reconnue au moment du load
                if (result.isEmpty()) {
                    runCatching { HttpClient.webApi.scheduleByDate(date).toGames() }
                        .onSuccess { list ->
                            result = list
                            Log.d("NHL", "score/$date ok, ${list.size} matchs")
                        }
                        .onFailure { e ->
                            Log.e("NHL", "score/$date failed", e)
                        }
                }

                _games.value = result
            } finally {
                _loading.value = false
            }
        }
    }
}
