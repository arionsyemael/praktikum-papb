package com.tifd.projectcomposed.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun MatkulScreen() {
    val db = Firebase.firestore
    val dataListState = remember { mutableStateOf(listOf<DataModel>()) }

    LaunchedEffect(Unit) {
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
                        null // Safely handle any conversion issues
                    }
                }
                dataListState.value = items.sortedWith(
                    compareBy<DataModel> { it.hari.urutan }
                        .thenBy { it.jam_mulai }
                )
            }
            .addOnFailureListener {
                // Handle the failure
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dataListState.value) { data ->
                DataCard(data)
            }
        }
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
