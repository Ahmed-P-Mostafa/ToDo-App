package com.polotika.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.polotika.todoapp.data.models.NoteModel

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(noteModel: NoteModel)

    @Update
    suspend fun updateNote(noteModel: NoteModel)

    @Delete
    suspend fun deleteNote(noteModel: NoteModel)

    @Query("select * from notes_table order by id ASC")
    fun getAllNotes():LiveData<List<NoteModel>>

    @Query("delete from notes_table")
    suspend fun deleteAllNotes()
}