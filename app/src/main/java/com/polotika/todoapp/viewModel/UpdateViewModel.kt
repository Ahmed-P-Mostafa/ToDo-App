package com.polotika.todoapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.data.repository.NotesRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    repository: NotesRepository,
    private val dispatcher: Dispatchers
) : BaseViewModel(repository = repository, dispatcher = dispatcher) {

    val title = MutableStateFlow("")
    val body = MutableStateFlow("")
    val priority = MutableLiveData<String>()


    fun updateNote(note: NoteModel) {
        viewModelScope.launch(dispatcher.IO) {
            repository.updateNote(note)
        }
    }

    val com = combine(title, body) { title, body ->
        return@combine Pair(title, body)
    }

}