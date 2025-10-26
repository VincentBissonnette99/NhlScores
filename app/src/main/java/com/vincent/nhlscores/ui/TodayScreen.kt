package com.vincent.nhlscores.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vincent.nhlscores.model.Game
import com.vincent.nhlscores.model.GameStatus
import com.vincent.nhlscores.viewmodel.TodayViewModel
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(onGameClick: (Long) -> Unit = {}, vm: TodayViewModel = viewModel()) {
    val games by vm.games.collectAsState()
    val refreshing by vm.loading.collectAsState()
    val state = rememberSwipeRefreshState(isRefreshing = refreshing)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Matchs du jour") }) }
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
                items(games) { g ->
                    ListItem(
                        headlineContent = { /* inchangé */ },
                        supportingContent = { Text(formatStatus(g)) },
                        modifier = Modifier.clickable { onGameClick(g.id) }   // navigate
                    )
                    Divider()
                }
            }
        }
    }
}

private fun formatStatus(g: Game): String {
    return when (g.status) {
        GameStatus.PRE -> {
            val local = g.startTimeUtc?.let { utc ->
                try {
                    val odt = OffsetDateTime.parse(utc)
                    val localTime = odt.atZoneSameInstant(ZoneId.of("America/Toronto")).toLocalTime()
                    localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                } catch (_: Throwable) { null }
            }
            if (local != null) "À $local" else "À venir"
        }
        GameStatus.LIVE -> {
            val label = periodLabel(g.period)
            val time = g.timeRemaining ?: ""
            if (time.isNotBlank()) "$label $time" else label
        }
        GameStatus.INTERMISSION -> {
            val label = periodLabel(g.period)
            "INT $label"
        }
        GameStatus.FINAL -> "Final"
    }
}

private fun periodLabel(period: Int?): String {
    val p = period ?: return "P?"
    return when (p) {
        1, 2, 3 -> "P$p"
        4 -> "OT"
        5 -> "SO"
        else -> "P$p"
    }
}
