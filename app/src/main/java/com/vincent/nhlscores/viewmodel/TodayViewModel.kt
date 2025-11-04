package com.vincent.nhlscores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.nhlscores.data.remote.HttpClient
import com.vincent.nhlscores.data.remote.NhlWebApi
import com.vincent.nhlscores.data.remote.WebScoreNowDto
import com.vincent.nhlscores.model.Game
import com.vincent.nhlscores.data.remote.toGames
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class TodayUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val games: List<Game> = emptyList()
)

class TodayViewModel : ViewModel() {

    private val api: NhlWebApi = HttpClient.web

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState

    init {
        loadInitial()
    }

    private fun todayUtcString(): String {
        val today = LocalDate.now(ZoneOffset.UTC)
        return today.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun refresh() {
        loadForDate(todayUtcString())
    }

    private fun loadInitial() {
        if (_uiState.value.games.isEmpty()) {
            refresh()
        }
    }

    private fun loadForDate(dateStr: String) {
        viewModelScope.launch {
            _uiState.value = TodayUiState(isLoading = true)
            try {
                val dto: WebScoreNowDto = api.scoreByDate(dateStr)
                _uiState.value = TodayUiState(games = dto.toGames())
            } catch (t: Throwable) {
                _uiState.value = TodayUiState(
                    error = t.message ?: "Unknown error"
                )
            }
        }
    }
}