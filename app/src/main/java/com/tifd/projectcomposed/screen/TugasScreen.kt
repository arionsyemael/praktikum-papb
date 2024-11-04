package com.tifd.projectcomposed.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tifd.projectcomposed.local.TugasRepository
import com.tifd.projectcomposed.viewmodel.MainViewModel
import com.tifd.projectcomposed.viewmodel.MainViewModelFactory

@Composable
fun TugasScreen(tugasRepository: TugasRepository) {
    val mainViewModel : MainViewModel = viewModel(factory = MainViewModelFactory(tugasRepository))
    var matkul by remember { mutableStateOf("") }
    var detailTugas by remember { mutableStateOf("") }
    val tugasList by mainViewModel.tugasList.observeAsState(emptyList())

    var snackbarVisible by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    fun showSnackbar(message: String) {
        snackbarMessage = message
        snackbarVisible = true
    }

    Column(modifier = Modifier.fillMaxWidth()){
        Column(modifier = Modifier.weight(1f)){
            Text(text = "Add new Task", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = matkul,
                onValueChange = { matkul = it },
                label = {Text("Mata Kuliah")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = detailTugas,
                onValueChange = { detailTugas = it },
                label = {Text("Detail Tugas")},
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (matkul.isNotEmpty() && detailTugas.isNotEmpty()) {
                        mainViewModel.addTugas(matkul, detailTugas)
                        showSnackbar("Tugas berhasil ditambahkan!")
                        matkul = ""
                        detailTugas = ""
                    } else {
                        showSnackbar("Pastikan Nama dan Detail terisi!")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ){
                Text(text = "Tambahkan!")
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp)
        ){
            Text(text = "Task List", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            if (tugasList.isEmpty()){
                Text(text = "You're free for now.", style = MaterialTheme.typography.titleMedium)
            } else {
                for ((index, tugas) in tugasList.withIndex()) {
                    if (!tugas.selesai) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween, // Align items
                                verticalAlignment = Alignment.CenterVertically     // Center vertically
                            ) {
                                Column {
                                    Text(
                                        text = "Mata Kuliah: ${tugas.matkul}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Detail Tugas: ${tugas.detailTugas}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Checkbox(
                                    checked = tugas.selesai,
                                    onCheckedChange = { isChecked ->
                                        mainViewModel.updateTugas(tugas)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (snackbarVisible) {
        Snackbar(
            action = {
                TextButton(onClick = { snackbarVisible = false }) {
                    Text("Okay")
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(snackbarMessage)
        }
    }
}