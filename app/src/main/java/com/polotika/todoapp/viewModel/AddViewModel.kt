package com.polotika.todoapp.viewModel

import com.polotika.todoapp.pojo.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(repository: NotesRepository, dispatchers: Dispatchers) :
    BaseViewModel(repository = repository, dispatcher = dispatchers) {

}