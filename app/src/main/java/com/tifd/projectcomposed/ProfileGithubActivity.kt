package com.tifd.projectcomposed
//arion
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class GitHubUser(
    val login: String,
    val name: String?,
    val avatar_url: String,
    val followers: Int,
    val following: Int
)

interface GitHubApi {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUser
}

class GithubProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GithubProfileScreen("arionsyemael") // Ganti dengan username yang diinginkan
                }
            }
        }
    }
}

@Composable
fun GithubProfileScreen(username: String) {
    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }

    var user by remember { mutableStateOf<GitHubUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(username) {
        coroutineScope.launch {
            try {
                user = api.getUser(username)
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                isError = true
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (isError) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error loading profile", color = Color.Red)
        }
    } else {
        user?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(it.avatar_url),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(128.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = it.name ?: "No Name", fontWeight = FontWeight.Bold)
                Text(text = "@${it.login}", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Followers: ${it.followers}")
                Text(text = "Following: ${it.following}")
            }
        }
    }
}
