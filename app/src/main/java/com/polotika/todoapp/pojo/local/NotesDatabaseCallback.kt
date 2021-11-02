package com.polotika.todoapp.pojo.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class NotesDatabaseCallback :RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
    }
}