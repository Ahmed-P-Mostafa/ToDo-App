package com.polotika.todoapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.models.PriorityModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.local.AppPreferences
import com.polotika.todoapp.pojo.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: NotesRepository,
    private val dispatchers: Dispatchers,
    private val prefs: AppPreferences
) : BaseViewModel(dispatchers, repository) {
    private val TAG = "HomeViewModel"
    var savedInstance = false

    init {
        val d :Deferred<Boolean> = viewModelScope.async(dispatchers.IO){
            return@async prefs.isAppTourGuide().first()
        }

        viewModelScope.launch(dispatchers.IO) {
            if( d.await() ){
                val list = mutableListOf<NoteModel>()
                list.add(NoteModel(title = "Tasks",description = "Learn new thins\nDesign Things\nShare my work\nStay hydrated",priority = PriorityModel.High))
                list.add(NoteModel(title = "Groceries",description = "Cat food\nTomatoes\nTuna\nMilk",priority = PriorityModel.Low))
                list.add(NoteModel(title = "Travel",description = "Canada\nParis\nItaly\nSwitzerland",priority = PriorityModel.Low))
                list.add(NoteModel(title = "Reminder",description = "Feed the cat\nWater the plants\nGo to gym\nFinish last chapter",priority = PriorityModel.High))
                list.add(NoteModel(title = "Interview questions",description = "Ask for team size\nIf any senior in the team ask for his name to search for it on linked in\nHow many days in the week and working hours",priority = PriorityModel.High))
                notesChannel.send(list)
                uistate.postValue( true)
            }else{
                uistate.postValue(false)
            }
        }
    }

    val uistate = MutableLiveData<Boolean>()
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
        viewModelScope.launch(dispatchers.IO) {
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