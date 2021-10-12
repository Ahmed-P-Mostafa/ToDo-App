package com.polotika.todoapp.pojo.data.repository

import androidx.lifecycle.LiveData
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.local.NotesDao
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(private val notesDao: NotesDao) :NotesRepository {
    override fun getAllNotes():LiveData<List<NoteModel>> = notesDao.getAllNotes()

    override suspend fun insertNote(noteModel: NoteModel){
        notesDao.addNote(noteModel= noteModel)
    }

    override suspend fun updateNote(note: NoteModel) {
        notesDao.updateNote(noteModel = note)
    }

    override suspend fun deleteNote(note: NoteModel) {
        notesDao.deleteNote(note)
    }

    override suspend fun deleteAll(){
        notesDao.deleteAllNotes()
    }

    override fun searchInDatabase(query:String):LiveData<List<NoteModel>>{
        return notesDao.searchInDatabase(query = query)
    }

    override fun sortByHighPriority():LiveData<List<NoteModel>>{
        return notesDao.sortByHighPriority()
    }

    override fun sortByLowPriority():LiveData<List<NoteModel>>{
        return notesDao.sortByLowPriority()
    }
}