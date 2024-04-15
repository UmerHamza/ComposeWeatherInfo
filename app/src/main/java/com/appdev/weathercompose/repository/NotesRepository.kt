package com.appdev.weathercompose.repository

import com.appdev.weathercompose.R
import com.appdev.weathercompose.dal.local.NotesDAO
import com.appdev.weathercompose.dal.network.BaseDataSource
import com.appdev.weathercompose.info.generic.Resource
import com.appdev.weathercompose.info.notes.NotesInfo
import com.appdev.weathercompose.utils.AppUtils
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(
    private val notesDAO: NotesDAO
) : BaseDataSource() {

    fun fetchNotesList(): List<NotesInfo> {
        return notesDAO.getAllNotes()
    }

    suspend fun addNote(notesInfo: NotesInfo){
        notesDAO.insertNote(notesInfo)
    }

    suspend fun updateNote(notesInfo: NotesInfo){
        notesDAO.updateNote(notesInfo)
    }

    suspend fun deleteNote(notesInfo: NotesInfo){
        notesDAO.deleteNote(notesInfo)
    }
}