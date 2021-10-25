package com.polotika.todoapp.viewModel

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    repository: NotesRepository,
    private val dispatcher: Dispatchers
) : BaseViewModel(repository = repository, dispatcher = dispatcher) {

    private val TAG = "UpdateViewModel"
    var note = NoteModel()
    private val title = MutableStateFlow(note.title)
    private val body = MutableStateFlow(note.description)
    val updateFragmentState = MutableLiveData<UpdateFragmentState>(UpdateFragmentState.EmptyState)



    fun onUpdateClicked() {
        if (!note.title.isNullOrBlank()&& !note.description.isNullOrBlank()) {
            //val note = collectNote()
            updateNote(note)
            updateFragmentState.value = UpdateFragmentState.CompleteState
        } else
            updateFragmentState.value = UpdateFragmentState.EmptyDataState


    }

    fun onDeleteClicked() {
        Log.d(TAG, "onDeleteClicked: ")
        updateFragmentState.value = UpdateFragmentState.DeleteDialogState

    }

    fun onConfirmDeleteClicked(){
        deleteNote(note)
        updateFragmentState.value = UpdateFragmentState.ConfirmDeleteState
    }

    fun onShareClicked(){
        title.value = note.title
        body.value = note.description
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


}

 sealed class UpdateFragmentState() {
    object EmptyState : UpdateFragmentState()
    object EmptyDataState : UpdateFragmentState()
    object CompleteState : UpdateFragmentState()
    object DeleteDialogState:UpdateFragmentState()
    object ShareNoteState:UpdateFragmentState()
    object ConfirmDeleteState:UpdateFragmentState()
}