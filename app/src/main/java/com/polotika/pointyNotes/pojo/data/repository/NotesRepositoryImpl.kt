package com.polotika.pointyNotes.pojo.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.polotika.pointyNotes.pojo.data.models.NoteModel
import com.polotika.pointyNotes.pojo.local.NotesDao
import com.polotika.pointyNotes.pojo.utils.AppConstants
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(private val notesDao: NotesDao) :NotesRepository {

    override fun getAllNotes(sortingState:String):LiveData<List<NoteModel>>{
        return when(sortingState){
            AppConstants.sortByDate ->{
                MutableLiveData(sortByDate())
            }
            AppConstants.sortByImportanceLow -> {
                MutableLiveData(sortByLowPriority())
            }
            AppConstants.sortByImportanceHigh -> {
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
            AppConstants.sortByDate -> notesDao.searchInDatabaseWhereSortByDate(query = query)
           AppConstants.sortByImportanceHigh ->notesDao.searchInDatabaseWherePriorityHigh(query)
           AppConstants.sortByImportanceLow ->notesDao.searchInDatabaseWherePriorityLow(query)
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




