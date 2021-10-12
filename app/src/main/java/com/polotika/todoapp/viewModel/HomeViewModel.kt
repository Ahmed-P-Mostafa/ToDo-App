package com.polotika.todoapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepositoryImpl
import com.polotika.todoapp.pojo.local.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(repositoryImpl: NotesRepositoryImpl,application: Application) :BaseViewModel(application, repositoryImpl) {
    private val TAG = "HomeViewModel"

    val getAllNotes :LiveData<List<NoteModel>> = repositoryImpl.getAllNotes()

    val isEmptyList = MutableLiveData(false)


    fun deleteAllNotes(){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.deleteAll()
        }
    }

    fun deleteNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.deleteNote(note)
        }
    }

    fun searchInDatabase(query:String): LiveData<List<NoteModel>> {
        return repositoryImpl.searchInDatabase(query)
    }

    fun sortByHighPriority(): LiveData<List<NoteModel>> {
        return repositoryImpl.sortByHighPriority()
    }

    fun sortByLowPriority(): LiveData<List<NoteModel>> {
        return repositoryImpl.sortByLowPriority()
    }

    fun addNote(noteModel: NoteModel){
        viewModelScope.launch(Dispatchers.IO){

            repositoryImpl.insertNote(noteModel = noteModel)
        }

        Log.d(TAG, "addNote: note")

    }
}