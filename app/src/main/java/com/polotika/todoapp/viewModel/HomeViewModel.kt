package com.polotika.todoapp.viewModel

import android.content.Context
import android.content.IntentSender.SendIntentException
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.models.PriorityModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.local.AppPreferences
import com.polotika.todoapp.pojo.utils.AppConstants
import com.polotika.todoapp.ui.HomeFragment
import com.polotika.todoapp.ui.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val context: Context,
    repository: NotesRepository,
    private val dispatchers: Dispatchers,
    private val prefs: AppPreferences
) : BaseViewModel(dispatchers, repository) {
    private val TAG = "HomeViewModel"
    val REQ_CODE_UPDATE_VERSION = 330

    var notesList = MutableLiveData<List<NoteModel>>()
    private var sortingState: String = AppConstants.sortByDate
    private val sortFlow = MutableStateFlow(sortingState)
    private val searchFlow = MutableStateFlow<String>("")
    private var installStateUpdatedListener: InstallStateUpdatedListener?=null
    private var appUpdateManager:AppUpdateManager?=null
    val updateStateMessage = MutableLiveData<String>()

    val isTourGuideUiState = MutableLiveData<Boolean>()

    //TODO make the viewModel get only the list sorted from repository
    val isEmptyList = MutableLiveData(false)

    var sortingFlow: MutableStateFlow<String>
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
            } else {
                isTourGuideUiState.postValue(false)
            }
        }
    }

    fun getAllNotesSorted(sortingValue: String? = null) {
        if (sortingValue == null) {
            viewModelScope.launch {
                prefs.getSortState().collect {
                    sortingState = it
                    notesList.postValue(repository.getAllNotes(it).value)
                }
            }

        } else {
            val newList = repository.getAllNotes(sortingValue).value
            notesList.value = newList ?: emptyList()
        }
    }

    val sortedNotesList: Flow<List<NoteModel>> = combine(sortFlow, searchFlow) { sort, search ->

        var sortedlist: List<NoteModel> = emptyList()
        if (search != null && search.isNotEmpty()) {
            sortedlist = searchInDatabase(query = search).value ?: emptyList()
        }
        if (sort != sortingFlow.asLiveData().value) {
            sortedlist = repository.getAllNotes(sort).value ?: emptyList()
            sortingFlow.emit(sort)
        }

        return@combine sortedlist
    }

    fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
            isEmptyList.postValue(true)
        }
    }

    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d(TAG, token!!)
        })
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

    fun checkForAppUpdate() {
        if (isNetworkConnected() ){
            Log.d(TAG, "checkForAppUpdate: ")

            appUpdateManager = AppUpdateManagerFactory.create(context)
            val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo

            installStateUpdatedListener =
                InstallStateUpdatedListener { installState: InstallState ->
                    if (installState.installStatus() == InstallStatus.DOWNLOADED)
                        popupSnackbarForCompleteUpdateAndUnregister()
                    else Log.d(TAG, "checkForAppUpdate: app updated")
                }
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                Log.d(TAG, "checkForAppUpdate: ${appUpdateInfo.packageName() + appUpdateInfo.availableVersionCode()}")
                Log.d(TAG, "checkForAppUpdate: ${appUpdateInfo.updateAvailability().toString() + UpdateAvailability.UPDATE_AVAILABLE.toString()}")
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        appUpdateManager!!.registerListener(installStateUpdatedListener!!)
                        startAppUpdateFlexible(appUpdateInfo)
                    } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startAppUpdateImmediate(appUpdateInfo)
                    }
                }
            }
        }

    }

    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,  // The current activity making the update request.
                HomeFragment().requireActivity(),  // Include a request code to later monitor this update request.
            REQ_CODE_UPDATE_VERSION
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
            unregisterInstallStateUpdListener()
        }
    }

    private fun startAppUpdateImmediate(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,  // The current activity making the update request
                HomeFragment().requireActivity(),
                REQ_CODE_UPDATE_VERSION
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }


    private fun popupSnackbarForCompleteUpdateAndUnregister() {
        updateStateMessage.value = "An update has just been downloaded."


    }

    fun completeUpdate(){
        appUpdateManager?.completeUpdate()
        unregisterInstallStateUpdListener()
    }

    fun checkNewAppVersionState() {
        if (appUpdateManager!=null){
            appUpdateManager!!
                .getAppUpdateInfo()
                .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        Log.d(TAG, "checkNewAppVersionState: downloaded")
                        popupSnackbarForCompleteUpdateAndUnregister()
                    }
                    if (appUpdateInfo.updateAvailability()
                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    ) {
                        startAppUpdateImmediate(appUpdateInfo)
                        Log.d(TAG, "checkNewAppVersionState: in progress")
                    }
                }
        }

    }

    fun unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null) appUpdateManager!!.unregisterListener(
            installStateUpdatedListener!!
        )
    }

    private fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val connectivityManager = ContextCompat.getSystemService(
            context,
            ConnectivityManager::class.java
        ) as ConnectivityManager
        val isInternet =  cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
        Log.d(TAG, "isNetworkConnected: $isInternet")
        return isInternet
    }
    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("google.com")
            //You can replace it with your name
            Log.d(TAG, "isInternetAvailable: ${ipAddr.equals("")}")
            !ipAddr.equals("")
        } catch (e: Exception) {
            Log.d(TAG, "isInternetAvailable: false")
            false
        }
    }

}