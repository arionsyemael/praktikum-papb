package com.tifd.projectcomposed

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tifd.projectcomposed.local.TugasRepository
import com.tifd.projectcomposed.navigation.NavigationItem
import com.tifd.projectcomposed.navigation.Screen
import com.tifd.projectcomposed.screen.MatkulScreen
import com.tifd.projectcomposed.screen.ProfileScreen
import com.tifd.projectcomposed.screen.TugasScreen
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class MainActivity : ComponentActivity() {

    private val githubProfileViewModel: GithubProfileViewModel by viewModels()

    // Inisialisasi TugasRepository
    private lateinit var tugasRepository: TugasRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi tugasRepository dengan context aplikasi
        tugasRepository = TugasRepository(application)

        githubProfileViewModel.fetchGithubProfile("arionsyemael")
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityScreen(viewModel = githubProfileViewModel, tugasRepository = tugasRepository)
                }
            }
        }
    }
}

@Composable
fun MainActivityScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: GithubProfileViewModel,
    tugasRepository: TugasRepository // Tambahkan parameter ini
) {
    Scaffold(
        bottomBar = { BottomBar(navController) },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Matkul.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Matkul.route) {
                MatkulScreen()
            }
            composable(Screen.Tugas.route) {
                TugasScreen(tugasRepository = tugasRepository) // Oper parameter ini ke TugasScreen
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel)
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier
    ) {
        val navigationItems = listOf(
            NavigationItem(
                title = "Matkul",
                icon = Icons.Filled.DateRange,
                screen = Screen.Matkul
            ),
            NavigationItem(
                title = "Tugas",
                icon = Icons.Filled.Check,
                screen = Screen.Tugas
            ),
            NavigationItem(
                title = "Profile",
                icon = Icons.Filled.AccountCircle,
                screen = Screen.Profile
            )
        )
        navigationItems.map { item ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = { Text(text = item.title) },
                selected = false,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
