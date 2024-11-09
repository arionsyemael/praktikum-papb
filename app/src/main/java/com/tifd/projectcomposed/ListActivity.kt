package com.tifd.projectcomposed


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class ListActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                Scaffold(
                    topBar = {
                        ListActivityTopBar()
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                val intent = Intent(this@ListActivity, GithubProfileActivity::class.java)
                                startActivity(intent)
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Image(
                                painter = rememberImagePainter("https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"),
                                contentDescription = "GitHub Logo",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    content = { paddingValues ->
                        DataListScreen(modifier = Modifier.padding(paddingValues))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListActivityTopBar() {
    TopAppBar(
        title = { Text("Jadwal Kuliah") }
    )
}

@Composable
fun DataListScreen(modifier: Modifier = Modifier) {
    val db = FirebaseFirestore.getInstance()
    var dataList by remember { mutableStateOf(listOf<JadwalKuliahModel>()) }

    LaunchedEffect(Unit) {
        db.collection("jadwal-kuliah")
            .get()
            .addOnSuccessListener { result ->
                val items = result.documents.map { document ->
                    JadwalKuliahModel(
                        hari = document.getString("hari") ?: "",
                        jamMulai = document.getString("jam mulai") ?: "",
                        jamSelesai = document.getString("jam selesai") ?: "",
                        matkul = document.getString("matkul") ?: "",
                        ruang = document.getString("ruang") ?: ""
                    )
                }
                dataList = items
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dataList) { data ->
            JadwalKuliahCard(data)
        }
    }
}

@Composable
fun JadwalKuliahCard(data: JadwalKuliahModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Hari: ${data.hari}", style = MaterialTheme.typography.bodyMedium)
            Text("Jam Mulai: ${data.jamMulai}", style = MaterialTheme.typography.bodyMedium)
            Text("Jam Selesai: ${data.jamSelesai}", style = MaterialTheme.typography.bodyMedium)
            Text("Mata Kuliah: ${data.matkul}", style = MaterialTheme.typography.bodyMedium)
            Text("Ruang: ${data.ruang}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Data Model class to handle the updated fields
data class JadwalKuliahModel(
    val hari: String,
    val jamMulai: String,
    val jamSelesai: String,
    val matkul: String,
    val ruang: String
)
