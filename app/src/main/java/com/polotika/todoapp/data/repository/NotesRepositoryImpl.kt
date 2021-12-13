package com.polotika.todoapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.polotika.todoapp.data.models.NoteModel
import com.polotika.todoapp.data.local.NotesDao
import com.polotika.todoapp.utils.AppConstants
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(private val notesDao: NotesDao) :NotesRepository {

    override fun getAllNotes(sortingState:String):LiveData<List<NoteModel>>{
        return when(sortingState){
            AppConstants.SORT_BY_DATE_KEY ->{
                MutableLiveData(sortByDate())
            }
            AppConstants.SORT_BY_IMPORTANCE_LOW -> {
                MutableLiveData(sortByLowPriority())
            }
            AppConstants.SORT_BY_IMPORTANCE_HIGH -> {
               MutableLiveData(sortByHighPriority())
            }
            else -> {
                MutableLiveData(sortByDate())
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

    override fun searchInDatabase(query:String,sortingState: String):LiveData<List<NoteModel>>{

       return when(sortingState){
            AppConstants.SORT_BY_DATE_KEY -> notesDao.searchInDatabaseWhereSortByDate(query = query)
           AppConstants.SORT_BY_IMPORTANCE_HIGH ->notesDao.searchInDatabaseWherePriorityHigh(query)
           AppConstants.SORT_BY_IMPORTANCE_LOW ->notesDao.searchInDatabaseWherePriorityLow(query)
           else -> notesDao.searchInDatabaseWhereSortByDate(query)
       }
    }

    override fun sortByDate(): List<NoteModel> {
        return notesDao.sortByDate()
    }

    override fun sortByHighPriority():List<NoteModel>{
        return notesDao.sortByHighPriority()
    }

    override fun sortByLowPriority():List<NoteModel>{
        return notesDao.sortByLowPriority()
    }
}




