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
    val games: List<Game> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(ZoneOffset.UTC),
    val minDate: LocalDate = LocalDate.now(ZoneOffset.UTC).minusDays(7),
    val maxDate: LocalDate = LocalDate.now(ZoneOffset.UTC).plusDays(7)
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

    private fun dateToString(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun refresh() {
        loadForDate(_uiState.value.selectedDate)
    }

    fun selectDate(date: LocalDate) {
        loadForDate(date)
    }

    fun previousDay() {
        val currentDate = _uiState.value.selectedDate
        val minDate = _uiState.value.minDate
        if (currentDate > minDate) {
            loadForDate(currentDate.minusDays(1))
        }
    }

    fun nextDay() {
        val currentDate = _uiState.value.selectedDate
        val maxDate = _uiState.value.maxDate
        if (currentDate < maxDate) {
            loadForDate(currentDate.plusDays(1))
        }
    }

    fun goToToday() {
        loadForDate(LocalDate.now(ZoneOffset.UTC))
    }

    private fun loadInitial() {
        loadForDate(LocalDate.now(ZoneOffset.UTC))
    }

    private fun loadForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedDate = date
            )
            try {
                val dto: WebScoreNowDto = api.scoreByDate(dateToString(date))
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    games = dto.toGames()
                )
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = t.message ?: "Unknown error"
                )
            }
        }
    }
}