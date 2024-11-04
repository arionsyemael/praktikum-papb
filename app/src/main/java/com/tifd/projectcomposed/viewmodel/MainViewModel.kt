package com.tifd.projectcomposed.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tifd.projectcomposed.local.Tugas
import com.tifd.projectcomposed.local.TugasRepository
import kotlinx.coroutines.launch

class MainViewModel(private val tugasRepository: TugasRepository) : ViewModel() {

    private val _tugasList = tugasRepository.getALlTugas()
    val tugasList: LiveData<List<Tugas>> = _tugasList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchAllTugas()
    }

    private fun fetchAllTugas() {
        viewModelScope.launch {
            tugasRepository.getALlTugas()
        }
    }

    fun addTugas(matkul: String, detailTugas: String) {
        val newTugas = Tugas(matkul = matkul, detailTugas = detailTugas, selesai = false)
        viewModelScope.launch {
            tugasRepository.insertTugas(newTugas)
        }
    }

    fun updateTugas(tugas: Tugas) {
        viewModelScope.launch {
            tugasRepository.updateTugas(tugas)
        }
    }


}