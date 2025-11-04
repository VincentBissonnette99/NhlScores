package com.vincent.nhlscores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vincent.nhlscores.ui.TodayScreen
import com.vincent.nhlscores.ui.GameDetailScreen
import com.vincent.nhlscores.ui.theme.NhlScoresTheme
import com.vincent.nhlscores.viewmodel.TodayViewModel
import com.vincent.nhlscores.viewmodel.GameDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot() }
    }
}

@Composable
fun AppRoot() {
    NhlScoresTheme {
        Surface {
            val nav = rememberNavController()
            NavHost(navController = nav, startDestination = "today") {
                composable("today") {
                    val viewModel: TodayViewModel = viewModel()
                    TodayScreen(
                        viewModel = viewModel,
                        onGameClick = { id -> nav.navigate("detail/$id") }
                    )
                }
                composable(
                    route = "detail/{gameId}",
                    arguments = listOf(navArgument("gameId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getLong("gameId") ?: -1L
                    val viewModel: GameDetailViewModel = viewModel()
                    GameDetailScreen(
                        viewModel = viewModel,
                        gameId = id,
                        onBack = { nav.popBackStack() }
                    )
                }
            }
        }
    }
}
