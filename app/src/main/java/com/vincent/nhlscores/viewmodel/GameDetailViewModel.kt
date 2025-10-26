package com.vincent.nhlscores.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.nhlscores.data.remote.HttpClient
import com.vincent.nhlscores.data.remote.toDetail
import com.vincent.nhlscores.model.GameDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class GameDetailViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _detail = MutableStateFlow<GameDetail?>(null)
    val detail: StateFlow<GameDetail?> = _detail

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        load()
    }

    fun refresh() {
        if (_loading.value) return
        load()
    }

    private fun load() {
        val gameId = savedStateHandle.get<Long>("gameId") ?: return
        viewModelScope.launch {
            _loading.value = true
            try {
                val date = LocalDate.now(ZoneId.of("America/Toronto")).toString()
                val dto = HttpClient.webApi.scheduleByDate(date)
                val game = dto.games.firstOrNull { it.id == gameId || it.gamePk == gameId }
                if (game != null) {
                    _detail.value = game.toDetail()
                } else {
                    val now = HttpClient.webApi.scoreNow()
                    val g2 = now.games.firstOrNull { it.id == gameId || it.gamePk == gameId }
                    _detail.value = g2?.toDetail()
                }
            } catch (e: Throwable) {
                Log.e("NHL", "detail load failed", e)
                _detail.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}
