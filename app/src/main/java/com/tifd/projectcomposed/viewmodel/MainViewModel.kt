package com.tifd.projectcomposed.viewmodel

import androidx.compose.ui.window.application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tifd.projectcomposed.local.Tugas
import com.tifd.projectcomposed.local.TugasRepository
import kotlinx.coroutines.launch


class MainViewModel(private val tugasRepository: TugasRepository) : ViewModel() {

    private val tugasRepository: TugasRepository = TugasRepository(application)
    val allTugas: LiveData<List<Tugas>> = tugasRepository.tugasList

    fun addTugas(tugas: Tugas) {
        viewModelScope.launch {
            tugasRepository.insertTugas(tugas)
        }
    }

    fun deleteTugas(tugas: Tugas) {
        viewModelScope.launch {
            tugasRepository.deleteTugas(tugas)
        }
    }

    fun toggleCompletion(tugas: Tugas) {
        viewModelScope.launch {
            val updatedTugas = tugas.copy(completed = !tugas.completed)
            tugasRepository.updateTugas(updatedTugas)
        }
    }
}