package com.polotika.todoapp.pojo.data.repository

import androidx.lifecycle.LiveData
import com.polotika.todoapp.pojo.data.models.NoteModel

interface NotesRepository {

    fun getAllNotes(sortingState:String): LiveData<List<NoteModel>>

    suspend fun insertNote(noteModel: NoteModel)

    suspend fun updateNote(note: NoteModel)

    suspend fun deleteNote(note: NoteModel)

    suspend fun deleteAll()

    fun searchInDatabase(query:String): LiveData<List<NoteModel>>

    fun sortByDate():LiveData<List<NoteModel>>

    fun sortByHighPriority(): List<NoteModel>

    fun sortByLowPriority(): List<NoteModel>


}