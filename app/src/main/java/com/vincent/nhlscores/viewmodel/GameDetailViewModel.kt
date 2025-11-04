package com.vincent.nhlscores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.nhlscores.data.remote.HttpClient
import com.vincent.nhlscores.data.remote.NhlWebApi
import com.vincent.nhlscores.data.remote.BoxscoreDto
import com.vincent.nhlscores.data.remote.PlayByPlayDto
import com.vincent.nhlscores.model.GameDetail
import com.vincent.nhlscores.data.remote.toDetail
import com.vincent.nhlscores.data.remote.mergeDetailWithPbp
import com.vincent.nhlscores.data.remote.mergeDetailWithBox
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GameDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val detail: GameDetail? = null
)

class GameDetailViewModel : ViewModel() {

    private val api: NhlWebApi = HttpClient.web

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState

    fun load(gameId: Long) {
        viewModelScope.launch {
            _uiState.value = GameDetailUiState(isLoading = true)
            try {
                val gameInfo = try {
                    api.gameLanding(gameId)
                } catch (e: Exception) {
                    api.scoreNow().games.firstOrNull { it.id == gameId || it.gamePk == gameId }
                }

                if (gameInfo == null) {
                    _uiState.value = GameDetailUiState(error = "Game not found")
                    return@launch
                }

                var detail: GameDetail = gameInfo.toDetail()

                try {
                    val pbp: PlayByPlayDto = api.playByPlay(gameId)
                    detail = detail.mergeDetailWithPbp(pbp)
                } catch (e: Exception) {
                }

                try {
                    val box: BoxscoreDto = api.boxscore(gameId)
                    detail = detail.mergeDetailWithBox(box)
                } catch (e: Exception) {
                }

                _uiState.value = GameDetailUiState(detail = detail)
            } catch (t: Throwable) {
                _uiState.value = GameDetailUiState(
                    error = t.message ?: "Failed to load game detail"
                )
            }
        }
    }
}