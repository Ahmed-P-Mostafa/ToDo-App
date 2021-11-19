package com.polotika.todoapp.viewModel

import android.content.Context
import androidx.lifecycle.*
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.models.PriorityModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.local.AppPreferences
import com.polotika.todoapp.pojo.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    repository: NotesRepository,
    private val dispatchers: Dispatchers,
    private val prefs: AppPreferences
) : BaseViewModel(dispatchers, repository) {

    var notesList = MutableLiveData<List<NoteModel>>()
    private var sortingState: String = AppConstants.sortByDate
    private val sortFlow = MutableStateFlow(sortingState)
    private val searchFlow = MutableStateFlow<String>("")

    var sortingFlow :MutableStateFlow<String>
        get() {
            return prefs.getSortState() as MutableStateFlow<String>
        }
        set(value) {
            viewModelScope.launch {
                prefs.setSortState(value.first())
            }
        }

    init {

        val d: Deferred<Boolean> = viewModelScope.async(dispatchers.IO) {
            return@async prefs.isAppTourGuide().first()
        }

        viewModelScope.launch(dispatchers.IO) {

            prefs.getSortState().collect { sortState ->
                sortingState = sortState
            }
            if (d.await()) {
                addNote(
                    NoteModel(
                        title = "Tasks",
                        description = "Learn new thins\nDesign Things\nShare my work\nStay hydrated",
                        priority = PriorityModel.High
                    )
                )
                addNote(
                    NoteModel(
                        title = "Groceries",
                        description = "Cat food\nTomatoes\nTuna\nMilk",
                        priority = PriorityModel.Low
                    )
                )
                addNote(
                    NoteModel(
                        title = "Travel",
                        description = "Canada\nParis\nItaly\nSwitzerland",
                        priority = PriorityModel.Low
                    )
                )
                addNote(
                    NoteModel(
                        title = "Reminder",
                        description = "Feed the cat\nWater the plants\nGo to gym\nFinish last chapter",
                        priority = PriorityModel.High
                    )
                )
                addNote(
                    NoteModel(
                        title = "Interview questions",
                        description = "Ask for team size\nIf any senior in the team ask for his name to search for it on linked in\nHow many days in the week and working hours",
                        priority = PriorityModel.High
                    )
                )
                isTourGuideUiState.postValue(true)
            }
            else {
                isTourGuideUiState.postValue(false)
            }
        }
    }

    fun getAllNotesSorted(sortingValue: String? = null){
        if (sortingValue == null) {
            viewModelScope.launch {
                prefs.getSortState().collect {
                    sortingState = it
                    notesList.postValue( repository.getAllNotes(it).value)
                }
            }

        } else {
            val newList = repository.getAllNotes(sortingValue).value
            notesList.value = newList?: emptyList()
        }
    }

    val sortedNotesList :Flow<List<NoteModel>> = combine(sortFlow,searchFlow){ sort, search ->

        var sortedlist :List<NoteModel> = emptyList()
         if (search!=null&& search.isNotEmpty()){
             sortedlist =  searchInDatabase(query = search).value?: emptyList()
        }
        if (sort != sortingFlow.asLiveData().value){
           sortedlist =  repository.getAllNotes(sort).value?: emptyList()
            sortingFlow.emit(sort)
        }

        return@combine sortedlist
    }

    val isTourGuideUiState = MutableLiveData<Boolean>()
    //TODO make the viewModel get only the list sorted from repository

    val isEmptyList = MutableLiveData(false)


    fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
            isEmptyList.postValue(true)
        }
    }

    fun searchInDatabase(query: String): LiveData<List<NoteModel>> {
        return repository.searchInDatabase(query = query, sortingState = sortingState)
    }

    private fun changeNotesSortingType(newSort: String) {
        viewModelScope.launch(dispatchers.IO) {
            notesList.postValue(repository.getAllNotes(newSort).value)
            prefs.setSortState(newSort)
        }
    }

    fun sortByHighPriority() {
        changeNotesSortingType(AppConstants.sortByImportanceHigh)
    }

    fun sortByLowPriority() {
        changeNotesSortingType(AppConstants.sortByImportanceLow)
    }

    fun sortByDate() {
        changeNotesSortingType(AppConstants.sortByDate)
    }

    fun showCaseTourGuideFinished() {
        viewModelScope.launch(dispatchers.IO) {
            prefs.setAppTourState(false)
        }
    }

    private fun checkForAppUpdates(){
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        viewModelScope.launch {
            appUpdateManager.requestUpdateFlow().collect { updateResult->
                when(updateResult){
                    is AppUpdateResult.Available ->{

                    }
                    is AppUpdateResult.InProgress ->{

                    }
                    is AppUpdateResult.Downloaded ->{

                    }
                }
            }
        }

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
            }
        }
    }

}