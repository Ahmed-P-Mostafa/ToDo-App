package com.polotika.todoapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.local.AppPreferences
import com.polotika.todoapp.pojo.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: NotesRepository,
    private val dispatchers: Dispatchers,
    private val prefs: AppPreferences
) : BaseViewModel(dispatchers, repository) {
    private val TAG = "HomeViewModel"
    var savedInstance = false

    //TODO make the viewModel get only the list sorted from repository

    val sortingState by lazy {
        flow {
            emit(prefs.getSortState().first())
        }

    }

    val notesChannel = Channel<List<NoteModel>>()



    var notesList = MutableLiveData<List<NoteModel>>()

    fun getAllNotes(sortState: String): LiveData<List<NoteModel>> {
        return repository.getAllNotes(sortState)

    }

    fun getSortedNotes(sortState: String? = null) {
        Log.d(TAG, "getSortedNotes: $sortState")
        if (sortState == null) {
            viewModelScope.launch(dispatchers.IO) {
                notesList.postValue(repository.getAllNotes(sortingState.first()).value)
            }
        } else {
            notesList.value = repository.getAllNotes(sortState).value
        }
    }

    val isEmptyList = MutableLiveData(false)


    fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun searchInDatabase(query: String): LiveData<List<NoteModel>> {
        return repository.searchInDatabase(query)
    }

    fun sortByHighPriority(): LiveData<List<NoteModel>> {
        viewModelScope.launch {
            prefs.setSortState(AppConstants.sortByImportanceHigh)
        }
        return repository.getAllNotes(AppConstants.sortByImportanceHigh)
    }

    fun sortByLowPriority(): LiveData<List<NoteModel>> {
        viewModelScope.launch {
            prefs.setSortState(AppConstants.sortByImportanceLow)
        }
        return repository.getAllNotes(AppConstants.sortByImportanceLow)
    }

    fun sortByDate(): LiveData<List<NoteModel>> {
        viewModelScope.launch {
            prefs.setSortState(AppConstants.sortByDate)
        }
        return repository.getAllNotes(AppConstants.sortByDate)
    }

}