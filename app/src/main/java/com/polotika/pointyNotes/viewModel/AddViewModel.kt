package com.polotika.pointyNotes.viewModel

import androidx.lifecycle.MutableLiveData
import com.polotika.pointyNotes.pojo.data.models.NoteModel
import com.polotika.pointyNotes.pojo.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(repository: NotesRepository, dispatchers: Dispatchers) :
    BaseViewModel(repository = repository, dispatcher = dispatchers) {

        val title = MutableLiveData<String>()
        val body = MutableLiveData<String>()
        val priority = MutableLiveData<String>()

    val addNoteState = MutableStateFlow<AddNoteState>(AddNoteState.EmptyState)

    fun onAddClicked(){
        if (title.value !=null && body.value!=null) {
            val note = NoteModel(
                title = title.value.toString(),
                description = body.value.toString(),
                priority = getPriorityValue(priority.value.toString())
            )

            addNote(noteModel = note)
            addNoteState.value = AddNoteState.CompleteState

        } else {
            addNoteState.value = AddNoteState.EmptyDataState
        }
    }


    }

sealed class AddNoteState(){
    object EmptyState:AddNoteState()
    object EmptyDataState:AddNoteState()
    object CompleteState:AddNoteState()
}