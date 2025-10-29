package com.vincent.nhlscores.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vincent.nhlscores.model.Game
import com.vincent.nhlscores.model.GameStatus
import com.vincent.nhlscores.viewmodel.TodayViewModel
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    onGameClick: (Long) -> Unit = {},
    vm: TodayViewModel = viewModel()
) {
    val games by vm.games.collectAsState()
    val refreshing by vm.loading.collectAsState()
    val date by vm.currentDate.collectAsState()
    val state = rememberSwipeRefreshState(isRefreshing = refreshing)

    val uiFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(date.format(uiFormatter)) },
                navigationIcon = {
                    TextButton(onClick = { vm.goToPreviousDay() }) {
                        Text("<")
                    }
                },
                actions = {
                    TextButton(onClick = { vm.goToNextDay() }) {
                        Text(">")
                    }
                }
            )
        }
    ) { padding ->
        SwipeRefresh(
            state = state,
            onRefresh = { vm.refresh() },
            indicator = { s, trigger ->
                SwipeRefreshIndicator(
                    state = s,
                    refreshTriggerDistance = trigger
                )
            }
        ) {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                if (games.isEmpty() && !refreshing) {
                    item {
                        Spacer(Modifier.height(24.dp))
                        Text("No games for this date")
                    }
                }
                items(games) { g ->
                    GameRow(game = g, onClick = { onGameClick(g.id) })
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun GameRow(game: Game, onClick: () -> Unit) {
    val title = "${safeTeam(game.away)} ${game.awayScore}  @  ${safeTeam(game.home)} ${game.homeScore}"
    val subtitle = formatStatusLine(game)

    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(subtitle) },
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxSize()
    )
}

private fun safeTeam(name: String?): String {
    val s = name?.trim().orEmpty()
    return if (s.isNotEmpty()) s else "Team"
}

private fun formatStatusLine(g: Game): String {
    return when (g.status) {
        GameStatus.PRE -> {
            val local = g.startTimeUtc?.let { utc ->
                try {
                    val odt = OffsetDateTime.parse(utc)
                    val lt = odt.atZoneSameInstant(ZoneId.systemDefault()).toLocalTime()
                    lt.format(DateTimeFormatter.ofPattern("HH:mm"))
                } catch (_: Throwable) { null }
            }
            local ?: "Scheduled"
        }
        GameStatus.LIVE -> {
            val label = periodLabel(g.period)
            val time = g.timeRemaining?.takeIf { it.isNotBlank() } ?: ""
            if (time.isNotEmpty()) "$label $time" else label
        }
        GameStatus.INTERMISSION -> {
            val label = periodLabel(g.period)
            "Intermission $label"
        }
        GameStatus.FINAL -> "Final"
    }
}

fun periodLabel(period: Int?): String {
    val p = period ?: return "P?"
    return when (p) {
        1, 2, 3 -> "P$p"
        4 -> "OT"
        5 -> "SO"
        else -> "P$p"
    }
}
