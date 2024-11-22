package com.tifd.projectcomposed.screen

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tifd.projectcomposed.local.TugasRepository
import com.tifd.projectcomposed.viewmodel.MainViewModel
import com.tifd.projectcomposed.viewmodel.MainViewModelFactory

@Composable
fun CameraPreview(showCamera: Boolean, onCameraClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Launcher untuk meminta izin kamera
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
            onCameraClose()
        }
    }

    LaunchedEffect(showCamera) {
        if (showCamera) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (showCamera) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Error setting up camera: ${e.message}", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Button(
            onClick = onCameraClose,
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            Text("Tutup Kamera")
        }
    }
}

@Composable
fun TugasScreen(tugasRepository: TugasRepository) {
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(tugasRepository))
    var matkul by remember { mutableStateOf("") }
    var detailTugas by remember { mutableStateOf("") }
    val tugasList by mainViewModel.allTugas.observeAsState(emptyList())

    var showCamera by remember { mutableStateOf(false) }
    var snackbarVisible by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    fun showSnackbar(message: String) {
        snackbarMessage = message
        snackbarVisible = true
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tambah Tugas Baru", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = matkul,
            onValueChange = { matkul = it },
            label = { Text("Mata Kuliah") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = detailTugas,
            onValueChange = { detailTugas = it },
            label = { Text("Detail Tugas") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Button(
                onClick = { showCamera = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Buka Kamera")
            }

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
                modifier = Modifier.weight(1f)
            ) {
                Text("Tambahkan")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tugasList.isEmpty()) {
            Text("KOSONG TUGASMU DEK.", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn {
                items(tugasList) { tugas ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Mata Kuliah: ${tugas.namaMatkul}")
                                Text("Detail Tugas: ${tugas.detailTugas}")
                            }
                            Checkbox(
                                checked = tugas.completed,
                                onCheckedChange = {
                                    mainViewModel.toggleCompletion(tugas)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showCamera) {
            CameraPreview(showCamera = showCamera, onCameraClose = { showCamera = false })
        }

        if (snackbarVisible) {
            Snackbar(
                action = {
                    TextButton(onClick = { snackbarVisible = false }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(snackbarMessage)
            }
        }
    }
}
