package com.tifd.projectcomposed.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TugasDao {

    @Query("SELECT * FROM tugas_table")
    fun getAllTugas(): LiveData<List<Tugas>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTugas(tugas: Tugas)

    @Delete
    fun deleteTugas(tugas: Tugas)

    @Update
    fun updateTugas(tugas: Tugas)
}
