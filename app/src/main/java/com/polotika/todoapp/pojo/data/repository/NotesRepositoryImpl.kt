package com.polotika.todoapp.pojo.data.repository

import android.util.Log
import com.polotika.todoapp.pojo.utils.AppConstants
import androidx.lifecycle.LiveData
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.local.NotesDao
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(private val notesDao: NotesDao) :NotesRepository {
    override fun getAllNotes(sortingState:String):LiveData<List<NoteModel>> {
        Log.d("TAG", "getAllNotes: $sortingState")

        return when(sortingState){
            AppConstants.sortByDate ->{
                sortByDate()
            }
            AppConstants.sortByImportanceLow -> {
                sortByLowPriority()
            }
            AppConstants.sortByImportanceHigh -> {
               sortByHighPriority()
            }
            else -> {
                notesDao.sortByDate()
            }
        }

    }

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

    override fun sortByDate(): LiveData<List<NoteModel>> {
        return notesDao.sortByDate()
    }

    override fun sortByHighPriority():LiveData<List<NoteModel>>{
        return notesDao.sortByHighPriority()
    }

    override fun sortByLowPriority():LiveData<List<NoteModel>>{
        return notesDao.sortByLowPriority()
    }
}




