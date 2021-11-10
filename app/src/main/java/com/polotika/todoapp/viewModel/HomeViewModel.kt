package com.polotika.todoapp.viewModel

import androidx.lifecycle.*
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.models.PriorityModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.local.AppPreferences
import com.polotika.todoapp.pojo.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: NotesRepository,
    private val dispatchers: Dispatchers,
    private val prefs: AppPreferences
) : BaseViewModel(dispatchers, repository) {

    val notesList = MutableLiveData<List<NoteModel>>()


    init {

        val d :Deferred<Boolean> = viewModelScope.async(dispatchers.IO){
            return@async prefs.isAppTourGuide().first()
        }

        viewModelScope.launch(dispatchers.IO) {

            prefs.getSortState().collect { sortState ->
                notesList.postValue(repository.getAllNotes(sortState).value?: emptyList())

            }
            if( d.await() ){
                addNote(NoteModel(title = "Tasks",description = "Learn new thins\nDesign Things\nShare my work\nStay hydrated",priority = PriorityModel.High))
                addNote(NoteModel(title = "Groceries",description = "Cat food\nTomatoes\nTuna\nMilk",priority = PriorityModel.Low))
                addNote(NoteModel(title = "Travel",description = "Canada\nParis\nItaly\nSwitzerland",priority = PriorityModel.Low))
                addNote(NoteModel(title = "Reminder",description = "Feed the cat\nWater the plants\nGo to gym\nFinish last chapter",priority = PriorityModel.High))
                addNote(NoteModel(title = "Interview questions",description = "Ask for team size\nIf any senior in the team ask for his name to search for it on linked in\nHow many days in the week and working hours",priority = PriorityModel.High))
                isTourGuideUiState.postValue( true)
            }else{
                isTourGuideUiState.postValue(false)
            }
        }
    }

    val isTourGuideUiState = MutableLiveData<Boolean>()
    //TODO make the viewModel get only the list sorted from repository


    val isEmptyList = MutableLiveData(false)


    fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun searchInDatabase(query: String): LiveData<List<NoteModel>> {
        return repository.searchInDatabase(query)
    }

    private fun changeDataSorting(newSort:String){
        viewModelScope.launch(dispatchers.IO) {
            prefs.setSortState(AppConstants.sortByImportanceHigh)
        }
    }
    fun sortByHighPriority() {
        changeDataSorting(AppConstants.sortByImportanceHigh)
    }

    fun sortByLowPriority(){
        changeDataSorting(AppConstants.sortByImportanceLow)
    }

    fun sortByDate(){
        changeDataSorting(AppConstants.sortByDate)
    }

    fun showCaseTourGuideFinished() {
        viewModelScope.launch(dispatchers.IO) {
            prefs.setAppTourState(false)
        }
    }

}