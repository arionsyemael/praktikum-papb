package com.tifd.projectcomposed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.tifd.projectcomposed.navigation.NavigationItem
import com.tifd.projectcomposed.navigation.Screen
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tifd.projectcomposed.screen.ProfileScreen
import com.tifd.projectcomposed.screen.TugasScreen
import com.tifd.projectcomposed.screen.MatkulScreen


class MainActivity : ComponentActivity() {

    private val githubProfileViewModel: GithubProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        githubProfileViewModel.fetchGithubProfile("arionsyemael")
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityScreen(viewModel = githubProfileViewModel)
                }
            }
        }
    }
}

@Composable
fun MainActivityScreen(
    modifier : Modifier = Modifier,
    navController : NavHostController = rememberNavController(),
    viewModel: GithubProfileViewModel

) {
    Scaffold(
        bottomBar = {BottomBar(navController)},
        modifier = modifier
    ) {
            innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Matkul.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Matkul.route) {
                MatkulScreen()
            }
            composable(Screen.Tugas.route) {
                TugasScreen()
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
                    Icon(imageVector = item.icon,
                        contentDescription = item.title)
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