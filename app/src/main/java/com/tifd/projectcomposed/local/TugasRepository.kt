package com.tifd.projectcomposed.local

import android.app.Application
import androidx.lifecycle.LiveData
import com.tifd.projectcomposed.navigation.Screen
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TugasRepository(application: Application) {
    private val tugasDao: TugasDAO
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = TugasDB.getDatabase(application)
        tugasDao = db.tugasDao()
    }
    fun getALlTugas(): LiveData<List<Tugas>> = tugasDao.getAllTugas()
    fun insertTugas(tugas: Tugas) {
        executorService.execute {
            tugasDao.insertTugas(tugas)
        }
    }
    fun updateTugas(tugas: Tugas) {
        executorService.execute {
            tugasDao.updateTugas(tugas)
        }
    }
}