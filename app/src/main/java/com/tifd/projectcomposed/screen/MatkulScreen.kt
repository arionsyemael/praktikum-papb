package com.tifd.projectcomposed.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MatkulScreen(modifier: Modifier = Modifier) {
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