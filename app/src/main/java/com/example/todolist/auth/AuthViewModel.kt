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
        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Formato de email inválido")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { _authState.value = AuthState.Authenticated }
                .addOnFailureListener { _authState.value = AuthState.Error(translateError(it.message)) }
        }
    }

    fun signup(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email e senha não podem estar vazios")
            return
        }
        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Formato de email inválido")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("A senha deve ter no mínimo 6 caracteres")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { _authState.value = AuthState.Authenticated }
                .addOnFailureListener { _authState.value = AuthState.Error(translateError(it.message)) }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun translateError(message: String?): String {
        return when {
            message == null -> "Erro desconhecido"
            message.contains("password is invalid") || message.contains("wrong-password") ->
                "Senha incorreta"
            message.contains("no user record") || message.contains("user-not-found") ->
                "Usuário não encontrado"
            message.contains("email address is already") || message.contains("email-already-in-use") ->
                "Este email já está cadastrado"
            message.contains("badly formatted") || message.contains("invalid-email") ->
                "Formato de email inválido"
            message.contains("network error") || message.contains("network") ->
                "Erro de conexão. Verifique sua internet"
            message.contains("too many requests") ->
                "Muitas tentativas. Tente novamente mais tarde"
            else -> message
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