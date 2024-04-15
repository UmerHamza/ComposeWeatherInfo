package com.appdev.weathercompose.dal.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appdev.weathercompose.info.notes.NotesInfo

@Database(entities = [NotesInfo::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDAO
}