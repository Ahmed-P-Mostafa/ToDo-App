package com.polotika.todoapp.viewModel

import SingleLiveEvent
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.local.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


public class NotesViewModel(application: Application):SharedViewModel(application) {
    private val TAG = "NotesViewModel"

    private val notesDao = NoteDatabase.getInstance(application.applicationContext).notesDao()
    private val repository :NotesRepository = NotesRepository(notesDao = notesDao)

    val noteBodySingleLiveEvent = SingleLiveEvent<String>()
    val noteTitleSingleLiveEvent = SingleLiveEvent<String>()


    val getAllNotes :LiveData<List<NoteModel>> = repository.getAllNotes()
    val isEmptyList = MutableLiveData(false)



    val title = MutableStateFlow("")
    val body = MutableStateFlow("")
    val priority = MutableLiveData<String>()

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

    val com = combine(title,body) { title, body ->
        return@combine Pair(title, body)
    }

    fun shareNote() {


    }
}