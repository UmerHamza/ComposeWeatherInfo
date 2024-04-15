package com.appdev.weathercompose.dal.local

import androidx.room.*
import com.appdev.weathercompose.info.notes.NotesInfo

@Dao
interface NotesDAO {

    @Insert
    suspend fun insertNote(notesInfo: NotesInfo)

    @Update
    suspend fun updateNote(notesInfo: NotesInfo)

    @Delete
    suspend fun deleteNote(notesInfo: NotesInfo)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<NotesInfo>

}