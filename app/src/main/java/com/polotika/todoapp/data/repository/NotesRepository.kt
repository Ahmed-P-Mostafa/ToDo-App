package com.polotika.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.polotika.todoapp.data.NotesDao
import com.polotika.todoapp.data.models.NoteModel

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

}