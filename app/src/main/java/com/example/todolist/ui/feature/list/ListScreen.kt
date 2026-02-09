package com.example.todolist.ui.feature.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.domain.Todo
import com.example.todolist.ui.UiEvent
import com.example.todolist.ui.components.TodoItem

@Composable
fun ListScreen(
    navigateToAddEditScreen: (id: Long?) -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.Navigate -> navigateToAddEditScreen(uiEvent.route.id)
                UiEvent.NavigateToLogin -> navigateToLogin()
                else -> {}
            }
        }
    }

    ListContent(
        todos = todos,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContent(
    todos: List<Todo>,
    onEvent: (ListEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Tarefas") },
                actions = {
                    IconButton(onClick = { onEvent(ListEvent.Logout) }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Sair"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(ListEvent.AddEdit(null)) }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(todos) { index, todo ->
                TodoItem(
                    todo = todo,
                    onCompletedChange = { isCompleted ->
                        onEvent(ListEvent.CompleteChanged(todo.id, isCompleted))
                    },
                    onItemClick = { onEvent(ListEvent.AddEdit(todo.id)) },
                    onDeleteClick = { onEvent(ListEvent.Delete(todo.id)) }
                )

                if (index < todos.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}