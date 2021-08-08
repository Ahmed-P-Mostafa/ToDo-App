package com.polotika.todoapp.pojo.data.repository

import androidx.lifecycle.LiveData
import com.polotika.todoapp.pojo.local.NotesDao
import com.polotika.todoapp.pojo.data.models.NoteModel

class NotesRepository(private val notesDao: NotesDao) {
    fun getAllNotes():LiveData<List<NoteModel>> = notesDao.getAllNotes()

    suspend fun insertNote(noteModel: NoteModel){
        notesDao.addNote(noteModel= noteModel)
    }

    suspend fun updateNote(note: NoteModel) {
        notesDao.updateNote(noteModel = note)
    }

    suspend fun deleteNote(note: NoteModel) {
        notesDao.deleteNote(note)
    }

    suspend fun deleteAll(){
        notesDao.deleteAllNotes()
    }

    fun searchInDatabase(query:String):LiveData<List<NoteModel>>{
        return notesDao.searchInDatabase(query = query)
    }

    fun sortByHighPriority():LiveData<List<NoteModel>>{
        return notesDao.sortByHighPriority()
    }

    fun sortByLowPriority():LiveData<List<NoteModel>>{
        return notesDao.sortByLowPriority()
    }

}