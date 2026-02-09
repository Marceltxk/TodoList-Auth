package com.example.todolist.ui.feature.addEdit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.todolist.data.TodoRepository
import com.example.todolist.navigation.AddEditRoute
import com.example.todolist.ui.UiEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: TodoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    private val id: Long? = savedStateHandle.toRoute<AddEditRoute>().id

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf<String?>(null)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        id?.let {
            viewModelScope.launch {
                val todo = repository.getBy(id)
                title = todo?.title ?: ""
                description = todo?.description
            }
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.TitleChanged -> title = event.title
            is AddEditEvent.DescriptionChanged -> description = event.description
            AddEditEvent.Save -> saveTodo()
        }
    }

    private fun saveTodo() {
        viewModelScope.launch {
            if (title.isBlank()) {
                _uiEvent.send(UiEvent.ShowSnackbar("Título não pode estar vazio"))
                return@launch
            }

            repository.insert(title, description, userId, id)
            _uiEvent.send(UiEvent.NavigateBack)
        }
    }
}