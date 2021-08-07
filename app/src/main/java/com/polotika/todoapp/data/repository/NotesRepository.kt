package com.polotika.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.polotika.todoapp.data.NotesDao
import com.polotika.todoapp.data.models.NoteModel

class NotesRepository(private val notesDao: NotesDao) {
    fun getAllNotes():List<NoteModel> = notesDao.getAllNotes()

    suspend fun insertNote(noteModel: NoteModel){
        notesDao.addNote(noteModel= noteModel)
    }

}