package com.example.todolist.ui.feature.addEdit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.ui.UiEvent

@Composable
fun AddEditScreen(
    navigateBack: () -> Unit,
    viewModel: AddEditViewModel = hiltViewModel(),
) {
    val title = viewModel.title
    val description = viewModel.description

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message,
                    )
                }

                UiEvent.NavigateBack -> {
                    navigateBack()
                }

                else -> Unit
            }
        }
    }

    AddEditContent(
        title = title,
        description = description,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContent(
    title: String,
    description: String?,
    snackbarHostState: SnackbarHostState,
    onEvent: (AddEditEvent) -> Unit,
    navigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (title.isEmpty()) "Nova Tarefa" else "Editar Tarefa") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    onEvent(AddEditEvent.Save)
                },
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                text = { Text("Salvar") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = {
                    if (it.length <= 100) {
                        onEvent(AddEditEvent.TitleChanged(it))
                    }
                },
                label = { Text("Título") },
                placeholder = { Text("O que precisa ser feito?") },
                singleLine = true,
                supportingText = {
                    Text(
                        text = "${title.length}/100",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                value = description ?: "",
                onValueChange = {
                    if (it.length <= 500) {
                        onEvent(AddEditEvent.DescriptionChanged(it))
                    }
                },
                label = { Text("Descrição") },
                placeholder = { Text("Adicione detalhes (opcional)") },
                maxLines = 8,
                supportingText = {
                    Text(
                        text = "${description?.length ?: 0}/500",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (title.isNotBlank()) {
                Text(
                    text = "💡 Dica: Pressione o botão Salvar para concluir",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AddEditContentPreview() {
    TodoListTheme {
        AddEditContent(
            title = "",
            description = null,
            snackbarHostState = SnackbarHostState(),
            onEvent = {},
        )
    }
}

@Composable
fun TodoListTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}