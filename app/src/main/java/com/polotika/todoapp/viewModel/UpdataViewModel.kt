package com.polotika.todoapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.polotika.todoapp.pojo.data.models.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UpdataViewModel :BaseViewModel() {

    val title = MutableStateFlow("")
    val body = MutableStateFlow("")
    val priority = MutableLiveData<String>()


    fun updateNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.updateNote(note)
        }
    }

    val com = combine(title, body) { title, body ->
        return@combine Pair(title, body)
    }

}