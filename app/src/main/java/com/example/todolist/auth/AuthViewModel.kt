package com.example.todolist.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email e senha não podem estar vazios")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { _authState.value = AuthState.Authenticated }
                .addOnFailureListener { _authState.value = AuthState.Error(it.message ?: "Erro ao fazer login") }
        }
    }

    fun signup(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email e senha não podem estar vazios")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { _authState.value = AuthState.Authenticated }
                .addOnFailureListener { _authState.value = AuthState.Error(it.message ?: "Erro ao criar conta") }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}