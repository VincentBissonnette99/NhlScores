package com.vincent.nhlscores.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vincent.nhlscores.model.Game
import com.vincent.nhlscores.model.isFinal
import com.vincent.nhlscores.model.isLive
import com.vincent.nhlscores.viewmodel.TodayViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onGameClick: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NHL Scores") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Column(
                        Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.games) { g ->
                            GameRow(game = g) { onGameClick(g.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameRow(game: Game, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${game.away} at ${game.home}", style = MaterialTheme.typography.titleMedium)
                Text(
                    when {
                        game.isFinal -> "Final, ${game.awayScore} - ${game.homeScore}"
                        game.isLive -> "P${game.period} ${game.timeRemaining ?: ""}, ${game.awayScore} - ${game.homeScore}"
                        else -> formatGameTime(game.startTimeUtc)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun formatGameTime(startTimeUtc: String?): String {
    if (startTimeUtc == null) return "Scheduled"

    return try {
        val utcTime = ZonedDateTime.parse(startTimeUtc)

        val localTime = utcTime.withZoneSameInstant(ZoneId.systemDefault())

        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        localTime.format(timeFormatter)
    } catch (e: Exception) {
        "Scheduled"
    }
}