package com.example.todolist.ui.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TodoRepository
import com.example.todolist.navigation.AddEditRoute
import com.example.todolist.ui.UiEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: TodoRepository,
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    val todos = repository.getAll(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: ListEvent) {
        when (event) {
            is ListEvent.Delete -> delete(event.id)
            is ListEvent.CompleteChanged -> completeChanged(event.id, event.isCompleted)
            is ListEvent.AddEdit -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(AddEditRoute(event.id)))
                }
            }
            ListEvent.Logout -> logout()
        }
    }

    private fun delete(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    private fun completeChanged(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateCompleted(id, isCompleted)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.NavigateToLogin)
        }
    }
}