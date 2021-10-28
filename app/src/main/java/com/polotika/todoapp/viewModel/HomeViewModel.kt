package com.polotika.todoapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.data.repository.NotesRepositoryImpl
import com.polotika.todoapp.pojo.local.AppPreferences
import com.polotika.todoapp.pojo.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: NotesRepository,
    private val dispatchers: Dispatchers,
    private val prefs:AppPreferences
) : BaseViewModel(dispatchers, repository) {

    var savedInstance = false

    var sortingState = flow {
        emit(prefs.getSortState().first())
    }

    private val TAG = "HomeViewModel"


    var notesList =MutableLiveData<List<NoteModel>>()

    fun getAllNotes(sortState:String):LiveData<List<NoteModel>>{
        savedInstance = true
       return repository.getAllNotes(sortState)
        //return notesList
     // return repository.getAllNotes(sortingState = sortState)
    }

    fun getSortedNotes(sortState:String? = null){
        Log.d(TAG, "getSortedNotes: $sortState")
        if (sortState==null){
            viewModelScope.launch(dispatchers.IO) {
                notesList.postValue( repository.getAllNotes(sortingState.first()).value )
            }
        }else{
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
        return repository.getAllNotes(AppConstants.sortByImportanceHigh)
    }

    fun sortByLowPriority(): LiveData<List<NoteModel>> {
        return repository.getAllNotes(AppConstants.sortByImportanceLow)
    }

    fun onSortClicked(sortState:String){
        getSortedNotes(sortState)

    }

}