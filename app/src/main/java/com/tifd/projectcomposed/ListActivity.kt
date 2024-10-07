package com.tifd.projectcomposed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme
import kotlinx.coroutines.launch

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DataListScreen()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DataListScreen() {
    val db = FirebaseFirestore.getInstance()
    LocalContext.current
    var dataList by remember { mutableStateOf(listOf<DataModel>()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current // for navigating to GitHubProfile

    // Fetch data on component mount
    LaunchedEffect(Unit) {
        loadDataFromFirestore(db, { data ->
            dataList = data
            isLoading = false
        }, {
            isLoading = false
            isError = true
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Failed to load data.")
            }
        })
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Navigate to GithubProfile Activity
                val intent = Intent(context, GithubProfileActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Person, contentDescription = "GitHub Profile")
            }
        }

    ) {
        if (isLoading) {
            LoadingView()
        } else if (isError) {
            ErrorView()
        } else {
            DataListView(dataList)
        }
    }
}

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error loading data", color = Color.Red, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DataListView(dataList: List<DataModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dataList) { data ->
            DataCard(data)
        }
    }
}

fun loadDataFromFirestore(
    db: FirebaseFirestore,
    onSuccess: (List<DataModel>) -> Unit,
    onFailure: () -> Unit
) {
    db.collection("jadwal-kuliah")
        .get()
        .addOnSuccessListener { result ->
            val items = result.documents.mapNotNull { document ->
                try {
                    DataModel(
                        mata_kuliah = document.getString("mata_kuliah") ?: "-",
                        hari = Hari.safeValueOf(document.getString("hari")),
                        jam_mulai = document.getString("jam_mulai") ?: "-",
                        jam_selesai = document.getString("jam_selesai") ?: "-",
                        ruang = document.getString("ruang") ?: "-"
                    )
                } catch (e: Exception) {
                    null // Handle potential conversion errors
                }
            }
            onSuccess(items.sortedWith(
                compareBy<DataModel> { it.hari.urutan }
                    .thenBy { it.jam_mulai }
            ))
        }
        .addOnFailureListener {
            onFailure()
        }
}

@Composable
fun DataCard(data: DataModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mata Kuliah: ${data.mata_kuliah}", style = MaterialTheme.typography.bodyMedium)
            Text("Hari: ${data.hari.name}", style = MaterialTheme.typography.bodyMedium)
            Text("Jam: ${data.jam_mulai} - ${data.jam_selesai}", style = MaterialTheme.typography.bodyMedium)
            Text("Ruang: ${data.ruang}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

data class DataModel(
    val mata_kuliah: String,
    val hari: Hari,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String
)

enum class Hari(val urutan: Int) {
    SENIN(1),
    SELASA(2),
    RABU(3),
    KAMIS(4),
    JUMAT(5),
    SABTU(6),
    MINGGU(7);

    companion object {
        fun safeValueOf(value: String?): Hari {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: SENIN
        }
    }
}
