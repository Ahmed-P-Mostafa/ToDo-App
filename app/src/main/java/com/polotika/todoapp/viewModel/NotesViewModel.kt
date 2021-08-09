package com.polotika.todoapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.polotika.todoapp.pojo.local.NoteDatabase
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public class NotesViewModel(application: Application):AndroidViewModel(application) {
    private val TAG = "NotesViewModel"

    private val notesDao = NoteDatabase.getInstance(application.applicationContext).notesDao()
    private val repository :NotesRepository
    val getAllNotes :LiveData<List<NoteModel>>
    val isEmptyList = MutableLiveData(false)



    val title = MutableLiveData<String>()
    val desc = MutableLiveData<String>()
    val priority = MutableLiveData<String>()

    init {
        repository = NotesRepository(notesDao = notesDao)
        getAllNotes = repository.getAllNotes()
    }

    fun addNote(noteModel: NoteModel){
        viewModelScope.launch(Dispatchers.IO){

            repository.insertNote(noteModel = noteModel)
        }

        Log.d(TAG, "addNote: note")

    }

    fun searchInDatabase(query:String):LiveData<List<NoteModel>>{
        return repository.searchInDatabase(query)
    }

    fun sortByHighPriority():LiveData<List<NoteModel>>{
        return repository.sortByHighPriority()
    }

    fun sortByLowPriority():LiveData<List<NoteModel>>{
        return repository.sortByLowPriority()
    }


    fun updateNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }
    fun deleteAllNotes(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}