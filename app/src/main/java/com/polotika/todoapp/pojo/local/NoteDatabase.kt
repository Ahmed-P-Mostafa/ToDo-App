package com.polotika.todoapp.pojo.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.utils.PriorityConverter

@Database(entities = [NoteModel::class], version = 3, exportSchema = false)
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
                    if (mINSTANCE ==null){
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