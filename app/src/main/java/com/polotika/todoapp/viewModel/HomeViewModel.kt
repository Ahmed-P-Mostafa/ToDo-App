package com.polotika.todoapp.viewModel

import androidx.lifecycle.*
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.data.repository.NotesRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: NotesRepository,
    private val dispatchers: Dispatchers
) : BaseViewModel(dispatchers, repository) {
    private val TAG = "HomeViewModel"

    val getAllNotes: LiveData<List<NoteModel>> = repository.getAllNotes()

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
        return repository.sortByHighPriority()
    }

    fun sortByLowPriority(): LiveData<List<NoteModel>> {
        return repository.sortByLowPriority()
    }

}