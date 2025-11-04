package com.vincent.nhlscores.ui

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
import com.vincent.nhlscores.model.GameDetail
import com.vincent.nhlscores.model.isFinal
import com.vincent.nhlscores.viewmodel.GameDetailViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    viewModel: GameDetailViewModel,
    gameId: Long,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffectOnce(key = gameId) {
        viewModel.load(gameId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> {
                    Column(Modifier.align(Alignment.Center)) {
                        Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                    }
                }
                state.detail != null -> DetailBody(state.detail!!)
            }
        }
    }
}

@Composable
private fun DetailBody(detail: GameDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("${detail.away} at ${detail.home}", style = MaterialTheme.typography.titleLarge)
            Text(
                if (detail.isFinal) "Final: ${detail.awayScore} - ${detail.homeScore}"
                else "Period ${detail.period} ${detail.timeRemaining ?: ""}: ${detail.awayScore} - ${detail.homeScore}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Shots on goal: ${detail.awaySog} - ${detail.homeSog}")
        }

        if (detail.goals.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text("Goals", style = MaterialTheme.typography.titleMedium)
            }
            items(detail.goals) { goal ->
                GoalCard(goal)
            }
        }
    }
}

@Composable
private fun GoalCard(goal: com.vincent.nhlscores.model.GoalEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Period ${goal.period} - ${goal.timeInPeriod}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    goal.team,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⚫ Goal: ", style = MaterialTheme.typography.labelMedium)
                Text(
                    goal.scorer,
                    style = MaterialTheme.typography.bodyLarge
                )
                goal.scorerSeasonTotal?.let { total ->
                    Text(
                        " (${total})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (goal.nthOfGame > 1) {
                Text(
                    "  ${goal.nthOfGame}${getSuffix(goal.nthOfGame)} goal of the game",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Assists
            if (goal.assists.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                goal.assists.forEachIndexed { index, assist ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🔵 Assist: ",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            assist,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        goal.assistsSeasonTotals.getOrNull(index)?.let { total ->
                            Text(
                                " ($total)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getSuffix(n: Int): String {
    return when {
        n % 100 in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
}

@Composable
private fun LaunchedEffectOnce(key: Any?, block: suspend () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(key1 = key) { block() }
}