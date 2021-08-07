package com.polotika.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.polotika.todoapp.data.models.NoteModel

@Database(entities = arrayOf(NoteModel::class), version = 1, exportSchema = false)
@TypeConverters(PriorityConverter::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {

        @Volatile
        private var mINSTANCE: NoteDatabase? = null

        @Synchronized
        fun getInstance(context: Context): NoteDatabase {
            if (mINSTANCE == null) {

                synchronized(NoteDatabase::class){
                    if (mINSTANCE==null){
                        mINSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            NoteDatabase::class.java,
                            "Notes_Database"
                        ).fallbackToDestructiveMigration()
                            .allowMainThreadQueries().build()

                    }

                }

            }
            return mINSTANCE as NoteDatabase
        }
    }
}