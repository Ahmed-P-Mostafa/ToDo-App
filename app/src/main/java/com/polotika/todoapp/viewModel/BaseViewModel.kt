package com.polotika.todoapp.viewModel

import android.app.Application
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polotika.todoapp.R
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.models.PriorityModel
import com.polotika.todoapp.pojo.data.repository.NotesRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(@ApplicationContext val application: Application, val repositoryImpl: NotesRepositoryImpl) : ViewModel() {
    private val TAG = "SharedViewModel"


    fun getPriority(priority: String): PriorityModel {
        return when (priority) {
            "High Priority" -> PriorityModel.High
            "Medium Priority" -> PriorityModel.Medium
            "Low Priority" -> PriorityModel.Low

            else -> PriorityModel.Low
        }
    }


    fun deleteNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.deleteNote(note)
        }
    }

    fun addNote(noteModel: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {

            repositoryImpl.insertNote(noteModel = noteModel)
        }

        Log.d(TAG, "addNote: note")

    }


    fun validateUserData(title: String, desc: String): Boolean {

        return !(TextUtils.isEmpty(title) || TextUtils.isEmpty(desc))
    }
}