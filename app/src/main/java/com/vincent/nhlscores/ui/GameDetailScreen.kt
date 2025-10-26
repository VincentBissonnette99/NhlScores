package com.vincent.nhlscores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vincent.nhlscores.model.GameDetail
import com.vincent.nhlscores.viewmodel.GameDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(gameId: Long, onBack: () -> Unit = {}) {
    val factory = viewModelFactory {
        initializer {
            val handle = SavedStateHandle(mapOf("gameId" to gameId))
            GameDetailViewModel(handle)
        }
    }
    val vm: GameDetailViewModel = viewModel(factory = factory)
    val detail by vm.detail.collectAsState()
    val loading by vm.loading.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Game Details") }) }
    ) { padding ->
        when {
            loading -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }

            detail == null -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) { Text("No data") }

            else -> DetailContent(detail!!, padding)
        }
    }
}

@Composable
private fun DetailContent(d: GameDetail, padding: PaddingValues) {
    LazyColumn(contentPadding = padding) {
        item {
            Text("${d.away} ${d.awayScore} @ ${d.home} ${d.homeScore}", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            val period = when (d.period ?: 0) {
                1,2,3 -> "P${d.period}"
                4 -> "OT"
                5 -> "SO"
                else -> "P?"
            }
            val time = d.timeRemaining ?: ""
            Text("Status: ${d.status}  $period $time")
            Spacer(Modifier.height(4.dp))
            Text("Shots: ${d.away} ${d.awaySog}  |  ${d.home} ${d.homeSog}")
            Spacer(Modifier.height(8.dp))
            Text("Goals", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Divider()
        }
        items(d.goals) { g ->
            val label = when (g.period) {
                1,2,3 -> "P${g.period}"
                4 -> "OT"
                5 -> "SO"
                else -> "P?"
            }
            val assists = if (g.assists.isNotEmpty()) "Assists: ${g.assists.joinToString(", ")}" else ""
            Column {
                Text("${g.timeInPeriod}  $label  ${g.team}")
                Text("${g.scorer}")
                if (assists.isNotBlank()) Text(assists)
                Divider()
            }
        }
    }
}