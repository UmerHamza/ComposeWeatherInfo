package com.appdev.weathercompose.info.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


data class NotesState(
    val isLoading: Boolean = false,
    val isEmptyState: Boolean = false,
    var errorMessage: String? = null,
    val notesInfo: ArrayList<NotesInfo>? = null
)

@Entity(tableName = "notes")
data class NotesInfo(
    @PrimaryKey(autoGenerate = true)
    var noteId: Int?,
    @ColumnInfo(name = "timeStamp")
    var timeStamp: String?,
    @ColumnInfo(name = "title")
    var title: String?,
    @ColumnInfo(name = "description")
    var description: String?,
    @ColumnInfo(name = "isDeleted")
    var isDeleted: Boolean?,
    @ColumnInfo(name = "deletionDate")
    var deletionDate: String?
):java.io.Serializable