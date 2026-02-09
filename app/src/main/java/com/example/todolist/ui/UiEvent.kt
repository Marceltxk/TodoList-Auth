package com.example.todolist.ui

import com.example.todolist.navigation.AddEditRoute

sealed class UiEvent {
    data class Navigate(val route: AddEditRoute) : UiEvent()
    object NavigateBack : UiEvent()
    object NavigateToLogin : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
}