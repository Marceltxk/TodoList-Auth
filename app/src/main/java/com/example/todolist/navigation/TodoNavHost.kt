package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.auth.AuthState
import com.example.todolist.auth.AuthViewModel
import com.example.todolist.auth.LoginScreen
import com.example.todolist.auth.SignupScreen
import com.example.todolist.ui.feature.addEdit.AddEditScreen
import com.example.todolist.ui.feature.list.ListScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object SignupRoute

@Serializable
object ListRoute

@Serializable
data class AddEditRoute(val id: Long? = null)

@Composable
fun TodoNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    val startDestination = if (authState is AuthState.Authenticated) ListRoute else LoginRoute

    NavHost(navController = navController, startDestination = startDestination) {
        composable<LoginRoute> {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(ListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate(SignupRoute) }
            )
        }

        composable<SignupRoute> {
            SignupScreen(
                viewModel = authViewModel,
                onSignupSuccess = {
                    navController.navigate(ListRoute) {
                        popUpTo(SignupRoute) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable<ListRoute> {
            ListScreen(
                authViewModel = authViewModel,
                navigateToAddEditScreen = { id ->
                    navController.navigate(AddEditRoute(id = id))
                },
                navigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<AddEditRoute> {
            AddEditScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
