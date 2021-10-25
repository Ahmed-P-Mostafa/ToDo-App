package com.polotika.todoapp.viewModel

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
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

    val id = MutableStateFlow(0)
    val title = MutableStateFlow("")
    val body = MutableStateFlow("")
    val priority = MutableLiveData<String>()
    val updateFragmentState = MutableStateFlow<UpdateFragmentState>(UpdateFragmentState.EmptyState)

    fun onUpdateClicked() {
        if (title.value.isNotBlank() && body.value.isNotBlank()) {
            val note = collectNote()
            updateNote(note)
            updateFragmentState.value = UpdateFragmentState.CompleteState
        } else
            updateFragmentState.value = UpdateFragmentState.EmptyDataState


    }

    fun onDeleteClicked() {
        updateFragmentState.value = UpdateFragmentState.DeleteDialogState
    }

    fun onConfirmDeleteClicked(){
        deleteNote(collectNote())
        updateFragmentState.value = UpdateFragmentState.ConfirmDeleteState
    }

    fun onShareClicked(){
        updateFragmentState.value = UpdateFragmentState.ShareNoteState
    }


    private fun updateNote(note: NoteModel) {
        viewModelScope.launch(dispatcher.IO) {
            repository.updateNote(note)
        }
    }



    val com = combine(title, body) { title, body ->
        return@combine Pair(title, body)
    }

    private fun collectNote():NoteModel{
        return NoteModel(
            id = id.value,
            title = title.value,
            description = body.value,
            priority = getPriorityValue(priority.value!!)
        )
    }

}

sealed class UpdateFragmentState() {
    object EmptyState : UpdateFragmentState()
    object EmptyDataState : UpdateFragmentState()
    object CompleteState : UpdateFragmentState()
    object DeleteDialogState:UpdateFragmentState()
    object ShareNoteState:UpdateFragmentState()
    object ConfirmDeleteState:UpdateFragmentState()
}